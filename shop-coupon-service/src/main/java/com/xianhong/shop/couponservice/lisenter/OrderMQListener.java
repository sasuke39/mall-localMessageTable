package com.xianhong.shop.couponservice.lisenter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianhong.shop.api.coupon.ShopCouponApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.common.mq.MQEntity;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.couponservice.service.TzCouponDao;
import com.xianhong.shop.pojo.TzCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author xianhong
 * @date 2022/1/5
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
                consumerGroup = "${mq.order.consumer.group.name}",
                messageModel = MessageModel.BROADCASTING,
                selectorExpression = "order_cancel||order_confirm")
public class OrderMQListener implements RocketMQListener<MessageExt> {


    @Resource
    private TzCouponDao tzCouponDao;

    @Resource
    private ShopCouponApi shopCouponApi;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag.cancel}")
    private String cancelTag;

    @Value("${mq.order.tag.confirm}")
    private String confirmTag;

    @Value("${rocketmq.producer.group}")
    private String mqProducerGroup;

    @Value("${mq.order.tag.confirm.callback}")
    private String callbackTag;


    @Override
    public void onMessage(MessageExt message) {

        try {
            //1. 解析消息内容
            String body = new String(message.getBody(), "UTF-8");
            String tags = message.getProperty("TAGS");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("优惠券接收到消息,{}",JSON.toJSON(mqEntity));
            //2. 查询优惠券信息
            TzCoupon coupon = tzCouponDao.getById(mqEntity.getCouponId());

            switch (tags){
                case "order_confirm":{
                    confirmCoupon(mqEntity);
                    MqOrderCallbackEntity mqEntity1 = MqOrderCallbackEntity.builder()
                            .couponId(coupon.getCouponId()).build();
                    sendMessage(topic,callbackTag, String.valueOf(mqEntity.getOrderId()),JSON.toJSONString(mqEntity1));
                    log.info("回调消息发送成功,{}",JSON.toJSONString(mqEntity1));
                    break;
                }
                case "order_cancel":{
                    cancelCoupon(coupon);
                    break;
                }
                default:{

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("优惠券消息消费失败");
        }

    }


    public void cancelCoupon(TzCoupon coupon){
        //幂等
        coupon.setUsedTime(null);
        coupon.setIsUsed(ShopCode.SHOP_COUPON_NO_USE.getCode());
        coupon.setOrderId(null);
        tzCouponDao.updateById(coupon);
        log.info("回退优惠券成功");
    }

    public void confirmCoupon(MQEntity mqEntity){
        //幂等
        TzCoupon coupon = tzCouponDao.getOne(new LambdaQueryWrapper<TzCoupon>().eq(TzCoupon::getCouponId, mqEntity.getCouponId()));
        //判断用户是否使用优惠券
        if (!Objects.isNull(coupon)){
            coupon.setIsUsed(ShopCode.SHOP_COUPON_USED.getCode());
            coupon.setUsedTime(LocalDateTime.now());
            coupon.setOrderId(mqEntity.getOrderId());
            FinalResponse<Boolean> finalResponse = shopCouponApi.reduceCoupon(coupon);
            if (!finalResponse.getData()) {
                //优惠券使用失败
                throw new ShopException("SHOP_COUPON_USE_FAIL");
            }

            log.info("订单:[" + mqEntity.getOrderId() + "]使用扣减优惠券[" + coupon.getCouponPrice() + "元]成功");
        }else {
            log.error("订单:[" + mqEntity.getOrderId() + "],减优惠券不存在"+mqEntity.getCouponId());
            throw new ShopException("SHOP_COUPON_USE_FAIL");
        }

    }

    private void sendMessage(String topic, String tags, String keys, String body) throws Exception {
        //判断Topic是否为空
        if (StringUtils.isEmpty(topic)) {
            throw new ShopException("SHOP_MQ_TOPIC_IS_EMPTY");
        }
        //判断消息内容是否为空
        if (StringUtils.isEmpty(body)) {
            throw new ShopException("SHOP_MQ_MESSAGE_BODY_IS_EMPTY");
        }
        //消息体
        Message message = new Message(topic, tags, keys, body.getBytes());
        //发送消息
        rocketMQTemplate.getProducer().send(message);
    }
}

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
            //1. ??????????????????
            String body = new String(message.getBody(), "UTF-8");
            String tags = message.getProperty("TAGS");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("????????????????????????,{}",JSON.toJSON(mqEntity));
            //2. ?????????????????????
            TzCoupon coupon = tzCouponDao.getById(mqEntity.getCouponId());

            switch (tags){
                case "order_confirm":{
                    confirmCoupon(mqEntity);
                    MqOrderCallbackEntity mqEntity1 = MqOrderCallbackEntity.builder()
                            .couponId(coupon.getCouponId()).build();
                    sendMessage(topic,callbackTag, String.valueOf(mqEntity.getOrderId()),JSON.toJSONString(mqEntity1));
                    log.info("????????????????????????,{}",JSON.toJSONString(mqEntity1));
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
            log.error("???????????????????????????");
        }

    }


    public void cancelCoupon(TzCoupon coupon){
        //??????
        coupon.setUsedTime(null);
        coupon.setIsUsed(ShopCode.SHOP_COUPON_NO_USE.getCode());
        coupon.setOrderId(null);
        tzCouponDao.updateById(coupon);
        log.info("?????????????????????");
    }

    public void confirmCoupon(MQEntity mqEntity){
        //??????
        TzCoupon coupon = tzCouponDao.getOne(new LambdaQueryWrapper<TzCoupon>().eq(TzCoupon::getCouponId, mqEntity.getCouponId()));
        //?????????????????????????????????
        if (!Objects.isNull(coupon)){
            coupon.setIsUsed(ShopCode.SHOP_COUPON_USED.getCode());
            coupon.setUsedTime(LocalDateTime.now());
            coupon.setOrderId(mqEntity.getOrderId());
            FinalResponse<Boolean> finalResponse = shopCouponApi.reduceCoupon(coupon);
            if (!finalResponse.getData()) {
                //?????????????????????
                throw new ShopException("SHOP_COUPON_USE_FAIL");
            }

            log.info("??????:[" + mqEntity.getOrderId() + "]?????????????????????[" + coupon.getCouponPrice() + "???]??????");
        }else {
            log.error("??????:[" + mqEntity.getOrderId() + "],?????????????????????"+mqEntity.getCouponId());
            throw new ShopException("SHOP_COUPON_USE_FAIL");
        }

    }

    private void sendMessage(String topic, String tags, String keys, String body) throws Exception {
        //??????Topic????????????
        if (StringUtils.isEmpty(topic)) {
            throw new ShopException("SHOP_MQ_TOPIC_IS_EMPTY");
        }
        //??????????????????????????????
        if (StringUtils.isEmpty(body)) {
            throw new ShopException("SHOP_MQ_MESSAGE_BODY_IS_EMPTY");
        }
        //?????????
        Message message = new Message(topic, tags, keys, body.getBytes());
        //????????????
        rocketMQTemplate.getProducer().send(message);
    }
}

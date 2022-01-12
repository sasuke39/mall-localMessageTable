package com.xianhong.shop.userservice.service.lisenter;

import com.alibaba.fastjson.JSON;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.common.mq.MQEntity;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.pojo.TzBalanceLog;
import com.xianhong.shop.userservice.service.TzBalanceLogDao;
import com.xianhong.shop.userservice.service.TzUserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * @author xianhong
 * @date 2022/1/5
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",consumerGroup = "${mq.order.consumer.group.name}",messageModel = MessageModel.BROADCASTING,
selectorExpression = "order_cancel||order_confirm")
public class UserOrderMQListener implements RocketMQListener<MessageExt> {


    @Resource
    private TzUserDao tzUserDao;
    @Resource
    private TzBalanceLogDao balanceLogDao;

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Value("${mq.order.topic}")
    private String topic;

    @Value("${rocketmq.producer.group}")
    private String mqProducerGroup;

    @Value("${mq.order.tag.confirm.callback}")
    private String callbackTag;

    @Override
    public void onMessage(MessageExt messageExt) {

        log.info("用户服务收到消息:{}", JSON.toJSONString(messageExt));
        String tags = messageExt.getTags();
        log.info("用户服务消息tags:,{}", tags);

        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        String keys = messageExt.getKeys();
        String msgId = messageExt.getMsgId();

        switch (tags){
            case "order_cancel":{
                try {
                    //1.解析消息
                    MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
                    log.info("接收到消息");
                    if(mqEntity.getUserMoney()!=null && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO)>0){
                        //2.调用业务层,进行余额修改
                        TzBalanceLog userMoneyLog = new TzBalanceLog();
                        userMoneyLog.setUseMoney(mqEntity.getUserMoney());
                        userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                        userMoneyLog.setUserId(mqEntity.getUserId());
                        userMoneyLog.setOrderId(mqEntity.getOrderId());
                        balanceLogDao.saveOrUpdate(userMoneyLog);
                        log.info("余额回退成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("余额回退失败");
                }
                break;
            }
            case "order_confirm":{
                MQEntity entity = JSON.parseObject(body, MQEntity.class);
                MqOrderCallbackEntity confirmGoods = MqOrderCallbackEntity.builder().userId(10L).build();
                try {
                    sendMessage(topic,callbackTag, String.valueOf(entity.getOrderId()),JSON.toJSONString(confirmGoods));
                    log.info("回调消息发送成功,{}",JSON.toJSONString(confirmGoods));
                } catch (Exception e) {
                    log.error("库存服务消费消息失败,{}",JSON.toJSONString(entity));
                    e.printStackTrace();
                }
                break;
            }
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

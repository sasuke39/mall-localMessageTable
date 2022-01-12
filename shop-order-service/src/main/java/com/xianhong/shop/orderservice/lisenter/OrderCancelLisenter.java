//package com.xianhong.shop.orderservice.lisenter;
//
//import com.alibaba.fastjson.JSON;
//import com.xianhong.shop.common.ShopCode;
//import com.xianhong.shop.common.mq.MQEntity;
//import com.xianhong.shop.message.CancelOrderMQ;
//import com.xianhong.shop.orderservice.service.TzOrderDao;
//import com.xianhong.shop.pojo.TzOrder;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.nio.charset.StandardCharsets;
//
///**
// * @author xianhong
// * @date 2022/1/5
// */
//@Slf4j
//@Component
//@RocketMQMessageListener(topic = "${mq.order.topic}",consumerGroup = "${mq.order.consumer.group.name}",messageModel = MessageModel.BROADCASTING,
//selectorExpression = "order_cancel")
//public class OrderCancelLisenter implements RocketMQListener<MessageExt> {
//
//    @Resource
//    TzOrderDao tzOrderDao;
//    @SneakyThrows
//    @Override
//    public void onMessage(MessageExt messageExt) {
//        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
//        String msgId = messageExt.getMsgId();
//        String tags = messageExt.getTags();
//        String keys = messageExt.getKeys();
//        log.info("CancelOrderProcessor receive message:"+messageExt);
//        CancelOrderMQ cancelOrderMQ = JSON.parseObject(body, CancelOrderMQ.class);
//        TzOrder order = tzOrderDao.getById(cancelOrderMQ.getOrderId());
//        order.setOrderStatus(ShopCode.SHOP_ORDER_CANCELED.getCode());
//        tzOrderDao.updateById(order);
//        log.info("订单:["+order.getOrderId()+"]状态设置为取消");
////        return order;
//    }
//}

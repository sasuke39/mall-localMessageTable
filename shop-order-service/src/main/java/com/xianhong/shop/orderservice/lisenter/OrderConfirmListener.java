package com.xianhong.shop.orderservice.lisenter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.mq.MQEntity;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.dao.TzMqConsumerMapper;
import com.xianhong.shop.dao.TzMqProduceMapper;
import com.xianhong.shop.message.CancelOrderMQ;
import com.xianhong.shop.orderservice.service.TzMqProducerDao;
import com.xianhong.shop.orderservice.service.TzOrderDao;
import com.xianhong.shop.pojo.TzGoods;
import com.xianhong.shop.pojo.TzMqProduce;
import com.xianhong.shop.pojo.TzOrder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * @author xianhong
 * @date 2022/1/11
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING,
        selectorExpression = "order_confirm_callback||order_cancel")
public class OrderConfirmListener implements RocketMQListener<MessageExt> {

    @Resource
    private TzMqProducerDao tzMqProducerDao;

    @Resource
    private TzMqProduceMapper tzMqProduceMapper;

    @Resource
    TzOrderDao tzOrderDao;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void onMessage(MessageExt messageExt) {
        String tags = messageExt.getTags();
        log.info("订单回调收到消息:{},tag:{}",JSON.toJSON(messageExt), tags);
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        boolean consumerSuccess =false;
        switch (tags){
            case "order_confirm_callback":{
                MqOrderCallbackEntity entity = JSON.parseObject(body, MqOrderCallbackEntity.class);
                //获取消息数据

                //更新消息表 什么字段更新什么
                while (!consumerSuccess) {
                    try {
                        TzMqProduce daoOne = tzMqProduceMapper.getById(messageExt.getKeys());
                        String ext = daoOne.getExt();
                        MqOrderCallbackEntity mqEntity = JSON.parseObject(ext, MqOrderCallbackEntity.class);
                        if (mqEntity==null){
                            mqEntity = MqOrderCallbackEntity.builder().build();
                        }
                        boolean b = invertEntity(entity, mqEntity);
                        daoOne.setExt(JSON.toJSONString(mqEntity));
                        daoOne.setMsgStatus(b ? ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode() : 0);
                        consumerSuccess=tzMqProduceMapper.updateProByLuckyLock(daoOne);
                        if (consumerSuccess){
                            log.info("更新本地消息表成功:{}",JSON.toJSONString(daoOne.getConsumeTimes()));
                        }else {
                            log.info("更新本地消息表失败:{}",JSON.toJSONString(daoOne.getConsumeTimes()));
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        log.error("订单回调消费失败");
                        e.printStackTrace();
                    }
                }
                break;
            }
            case "order_cancel" :{
                 body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                String msgId = messageExt.getMsgId();
                tags = messageExt.getTags();
                String keys = messageExt.getKeys();
                log.info("CancelOrderProcessor receive message:"+messageExt);
                CancelOrderMQ cancelOrderMQ = JSON.parseObject(body, CancelOrderMQ.class);
                TzOrder order = tzOrderDao.getById(cancelOrderMQ.getOrderId());
                order.setOrderStatus(ShopCode.SHOP_ORDER_CANCELED.getCode());
                tzOrderDao.updateById(order);
                log.info("订单:["+order.getOrderId()+"]状态设置为取消");
                break;
            }
            default:{

            }
        }


    }


    public boolean invertEntity(MqOrderCallbackEntity source,MqOrderCallbackEntity target) throws Exception {
        Long orderId = source.getOrderId();
        Long couponId = source.getCouponId();
        Long goodsId = source.getGoodsId();
        Long userId = source.getUserId();
        Long goodsLogId = source.getGoodsLogId();


        target.setOrderId(orderId == null ? target.getOrderId() :orderId);
        target.setCouponId(couponId==null? target.getCouponId():couponId);
        target.setUserId(userId==null?target.getUserId():userId);
        target.setGoodsId(goodsId==null?target.getGoodsId():goodsId);
        target.setGoodsLogId(goodsLogId==null?target.getGoodsLogId():goodsLogId);
        return isFillObject(target);

    }


    public static boolean isFillObject(Object object) throws Exception {
        boolean flag =true;
        Class<?> aClass = object.getClass();
        Field[] fields = aClass.getFields();

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.getName().equals("orderId")){
                continue;
            }
            Object var = field.get(object);
            if (var==null){
                flag=false;
                break;
            }
        }
        return flag;
    }



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

    public void cancelOrder(){

    }
}

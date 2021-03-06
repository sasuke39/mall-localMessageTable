package com.xianhong.shop.orderweb.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xianhong.shop.api.coupon.ShopCouponApi;
import com.xianhong.shop.api.goods.ShopGoodsApi;
import com.xianhong.shop.api.order.ShopOrderApi;
import com.xianhong.shop.api.user.ShopUserApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.common.mq.MQEntity;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.dao.TzMqProduceMapper;
import com.xianhong.shop.message.CancelOrderMQ;
import com.xianhong.shop.pojo.TzBalanceLog;
import com.xianhong.shop.pojo.TzCoupon;
import com.xianhong.shop.pojo.TzGoodsLog;
import com.xianhong.shop.pojo.TzMqProduce;
import com.xianhong.shop.pojo.TzOrder;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.req.OrderRequest;
import com.xianhong.shop.res.OrderResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@Component
@Slf4j
public class ShopOrderServiceImpl implements ShopOrderService {

    @Reference
    ShopOrderApi shopOrderApi;
    @Reference
    ShopCouponApi shopCouponApi;
    @Reference
    ShopUserApi shopUserApi;
    @Reference
    ShopGoodsApi shopGoodsApi;
    @Resource
    TzMqProduceMapper tzMqProduceMapper;

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

    @Override
    public OrderResponse confirmOrder(OrderRequest order) throws ShopException {
        log.info(JSON.toJSONString(order));
        TzOrder tzOrder = order.getOrder();
        checkOrder(tzOrder);
        //???????????????
        savePreOrder(tzOrder);
        try {
            //3.????????????
            reduceGoodsNum(tzOrder);
            //4.???????????????
            changeCoponStatus(tzOrder);
            //5.????????????
            reduceMoneyPaid(tzOrder);
            //6.????????????
            updateOrderStatus(tzOrder);
            log.info("??????:[" + JSON.toJSONString(order) + "]????????????");

        } catch (Exception e) {
            //??????????????????,????????????
            CancelOrderMQ cancelOrderMQ = new CancelOrderMQ();
            cancelOrderMQ.setOrderId(tzOrder.getOrderId());
            cancelOrderMQ.setCouponId(tzOrder.getCouponId());
            cancelOrderMQ.setGoodsId(tzOrder.getGoodsId());
            cancelOrderMQ.setGoodsNumber(tzOrder.getGoodsNumber());
            cancelOrderMQ.setUserId(tzOrder.getUserId());
            cancelOrderMQ.setUserMoney(tzOrder.getMoneyPaid());
            try {
                sendMessage(topic,
                        cancelTag,
                        cancelOrderMQ.getOrderId().toString(),
                        JSON.toJSONString(cancelOrderMQ));
            } catch (Exception e1) {
                e.printStackTrace();
                e1.printStackTrace();
                log.error("?????????????????????????????????topic:{},tag:{},keys:{},body:{}", topic, cancelTag, cancelOrderMQ.getOrderId().toString(), JSON.toJSONString(cancelOrderMQ));
                throw new ShopException("SHOP_MQ_SEND_MESSAGE_FAIL");
            }
        }

        return OrderResponse.builder().orderId(tzOrder.getOrderId()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse confirmOrderByNativeTable(OrderRequest order) throws Exception {
        TzOrder tzOrder = order.getOrder();

        checkOrder(tzOrder);
        //???????????????
        savePreOrder(tzOrder);

        MQEntity mqEntity = MQEntity.builder().orderId(tzOrder.getOrderId())
                .couponId(tzOrder.getCouponId())
                .goodsId(tzOrder.getGoodsId())
                .goodsNumber(tzOrder.getGoodsNumber())
                .userId(tzOrder.getUserId())
                .userMoney(tzOrder.getMoneyPaid()).build();

        String keys = tzOrder.getOrderId().toString();
        String body = JSON.toJSONString(mqEntity);
        //??????????????????
        MqOrderCallbackEntity callbackEntity = MqOrderCallbackEntity.builder().build();
        TzMqProduce tzMqProduce = TzMqProduce.builder().groupName(mqProducerGroup)
                .msgKey(keys)
                .msgStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode())
                .msgBody(body)
                .msgTag(confirmTag)
                .msgTopic(topic)
                .ext(JSON.toJSONString(callbackEntity))
                .createTime(LocalDateTime.now())
                .build();
        tzMqProduceMapper.insert(tzMqProduce);
        //????????????
        sendMessage(topic, confirmTag, keys, body);

        return OrderResponse.builder().orderId(tzOrder.getOrderId()).build();
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

    private void checkOrder(TzOrder request) throws ShopException {
        FinalResponse<TzUser> userId = shopUserApi.getUserInfoByUserId(request.getUserId());
        TzUser data = userId.getData();
        if (data == null) {
            throw new ShopException("??????????????????");
        }
    }

    private void savePreOrder(TzOrder order) throws ShopException {
        //1.??????????????????????????????
        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        //2.??????ID
//        order.setOrderId(idWorker.nextId());
        //????????????????????????
//        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
//        if (order.getShippingFee().compareTo(shippingFee) != 0) {
//            CastException.cast(ShopCode.SHOP_ORDER_SHIPPINGFEE_INVALID);
//        }
        //3.?????????????????????????????????
        BigDecimal orderAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
//        orderAmount.add(shippingFee);
//        if (orderAmount.compareTo(order.getOrderAmount()) != 0) {
//            CastException.cast(ShopCode.SHOP_ORDERAMOUNT_INVALID);
//        }

        //4.?????????????????????????????????
        Long couponId = order.getCouponId();
        if (couponId != null) {
            TzCoupon coupon = shopCouponApi.getCouponById(couponId).getData();
            //??????????????????
            if (coupon == null) {
                throw new ShopException("??????????????????");
            }
            //?????????????????????
            if ((ShopCode.SHOP_COUPON_USED.getCode())
                    == (coupon.getIsUsed())) {
                throw new ShopException("??????????????????");
            }
            order.setCouponPaid(coupon.getCouponPrice());
        } else {
            order.setCouponPaid(BigDecimal.ZERO);
        }

        //5.????????????????????????
        BigDecimal moneyPaid = order.getMoneyPaid();
        if (moneyPaid != null) {
            //????????????????????????0
            int r = order.getMoneyPaid().compareTo(BigDecimal.ZERO);
            //????????????0
            if (r < 0) {
                throw new ShopException("SHOP_MONEY_PAID_LESS_ZERO");
            }
            //????????????0
            if (r > 0) {
                //??????????????????
                TzUser user = shopUserApi.getUserInfoByUserId(order.getUserId()).getData();
                if (user == null) {
                    throw new ShopException("SHOP_USER_NO_EXIST");
                }
                //??????????????????????????????????????????
                if (user.getUserMoney().compareTo(BigDecimal.valueOf(order.getMoneyPaid().longValue())) < 0) {
                    throw new ShopException("SHOP_MONEY_PAID_INVALID");
                }
                order.setMoneyPaid(order.getMoneyPaid());
            }
        } else {
            order.setMoneyPaid(BigDecimal.ZERO);
        }
        //????????????????????????
        order.setPayAmount(orderAmount.subtract(order.getCouponPaid())
                .subtract(order.getMoneyPaid()));
        //????????????????????????
        order.setAddTime(LocalDateTime.now());

        //???????????????
        OrderResponse response = shopOrderApi.insertOrder(order);
        order.setOrderId(response.getOrderId());
        if (response.getOrderId() == null) {
            throw new ShopException("SHOP_ORDER_SAVE_ERROR");
        }
        log.info("??????:[" + order.getOrderId() + "]?????????????????????");
    }

    private void reduceGoodsNum(TzOrder order) {
        TzGoodsLog goodsNumberLog = TzGoodsLog.builder().build();
        goodsNumberLog.setGoodsId(Math.toIntExact(order.getGoodsId()));
        goodsNumberLog.setOrderId(String.valueOf(order.getOrderId()));
        goodsNumberLog.setGoodsNumber(order.getGoodsNumber());
        FinalResponse<Boolean> finalResponse = shopGoodsApi.reduceGoodsNum(goodsNumberLog);
        if (!finalResponse.getData()) {
            throw new ShopException("SHOP_REDUCE_GOODS_NUM_FAIL");
        }
        log.info("??????:[" + order.getOrderId() + "]????????????[" + order.getGoodsNumber() + "???]??????");
    }

    private void changeCoponStatus(TzOrder order) {
        //?????????????????????????????????
        if (!StringUtils.isEmpty(order.getCouponId())) {
            //?????????????????????
            TzCoupon coupon = shopCouponApi.getCouponById(order.getCouponId()).getData();
            coupon.setIsUsed(ShopCode.SHOP_COUPON_USED.getCode());
            coupon.setUsedTime(LocalDateTime.now());
            coupon.setOrderId(order.getOrderId());
            FinalResponse<Boolean> finalResponse = shopCouponApi.reduceCoupon(coupon);

            //??????????????????
            if (!finalResponse.getData()) {
                //?????????????????????
                throw new ShopException("SHOP_COUPON_USE_FAIL");
            }

            log.info("??????:[" + order.getOrderId() + "]?????????????????????[" + coupon.getCouponPrice() + "???]??????");
            throw new ShopException("SHOP_COUPON_USE_FAIL");
        }

    }

    private void reduceMoneyPaid(TzOrder order) {
        //??????????????????????????????????????????
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) > 0) {
            TzBalanceLog userMoneyLog = new TzBalanceLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            //????????????
            FinalResponse<Boolean> response = shopUserApi.changeUserMoney(userMoneyLog);
            if (!response.getData()) {
                throw new ShopException("SHOP_USER_MONEY_REDUCE_FAIL");
            }
            log.info("??????:[" + order.getOrderId() + "????????????[" + order.getMoneyPaid() + "???]??????]");
        }
    }

    private void updateOrderStatus(TzOrder order) {
        order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRMED.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_UN_PAY.getCode());
        order.setConfirmTime(LocalDateTime.now());
        Boolean data = shopOrderApi.updateOrder(order).getData();
        if (!data) {
            throw new ShopException("SHOP_ORDER_CONFIRM_FAIL");
        }
        log.info("??????:[" + order.getOrderId() + "]??????????????????");
    }


}

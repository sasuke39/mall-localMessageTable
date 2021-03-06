package com.xianhong.shop.goodsservice.lisenter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.common.mq.MQEntity;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.dao.TzGoodsMapper;
import com.xianhong.shop.dao.TzMqConsumerMapper;
import com.xianhong.shop.goodsservice.service.TzGoodsDao;
import com.xianhong.shop.goodsservice.service.TzGoodsLogDao;
import com.xianhong.shop.pojo.TzGoods;
import com.xianhong.shop.pojo.TzGoodsLog;
import com.xianhong.shop.pojo.TzMqConsumer;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author xianhong
 * @date 2022/1/5
 */
@Component
@Slf4j
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING,
        selectorExpression = "order_cancel||order_confirm")
public class GoodsOrderMQListener implements RocketMQListener<MessageExt> {

    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Resource
    private TzGoodsDao tzGoodsDao;

    @Resource
    private TzMqConsumerMapper tzMqConsumerMapper;

    @Resource
    private TzGoodsLogDao tzGoodsLogDao;

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Value("${mq.order.topic}")
    private String topic;

    @Value("${rocketmq.producer.group}")
    private String mqProducerGroup;

    @Value("${mq.order.tag.confirm.callback}")
    private String callbackTag;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(MessageExt messageExt) {
        log.info("????????????????????????:{}", JSON.toJSONString(messageExt));
        String tags = messageExt.getTags();
        log.info("??????????????????tags:,{}", tags);

        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        String keys = messageExt.getKeys();
        String msgId = messageExt.getMsgId();


        switch (tags) {
            case "order_confirm": {
                MQEntity entity = JSON.parseObject(body, MQEntity.class);
                MqOrderCallbackEntity confirmGoods = confirmGoods(entity);
                try {
                    sendMessage(topic,callbackTag, String.valueOf(entity.getOrderId()),JSON.toJSONString(confirmGoods));
                    log.info("????????????????????????,{}",JSON.toJSONString(confirmGoods));
                } catch (Exception e) {
                    log.error("??????????????????????????????,{}",JSON.toJSONString(entity));
                    e.printStackTrace();
                }

                break;
            }
            case "order_cancel": {
                putMessage(tags, body, keys, msgId);
            }

        }


    }

    public MqOrderCallbackEntity confirmGoods(MQEntity entity){
        TzGoods build = TzGoods.builder().goodsId(entity.getGoodsId()).goodsNumber(entity.getGoodsNumber()).build();
        return tzGoodsDao.confirmGoodsNew(entity.getUserId(),build,entity.getOrderId());
    }
    private void putMessage(String tags, String body, String keys, String msgId) {
        try {
            //1. ??????????????????

            log.info("??????????????????");

            //2. ????????????????????????
            LambdaQueryWrapper<TzMqConsumer> eq = new LambdaQueryWrapper<TzMqConsumer>()
                    .eq(TzMqConsumer::getMsgTag, tags).eq(TzMqConsumer::getMsgKey, keys).eq(TzMqConsumer::getGroupName, groupName);
            TzMqConsumer mqConsumerLog = tzMqConsumerMapper.selectOne(eq);

            if (mqConsumerLog != null) {
                //3. ?????????????????????...
                //3.1 ????????????????????????
                Integer status = mqConsumerLog.getConsumerStatus();
                //?????????...??????
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode() == status) {
                    log.info("??????:" + msgId + ",???????????????");
                    return;
                }

                //????????????...??????
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode() == status) {
                    log.info("??????:" + msgId + ",????????????");
                    return;
                }

                //????????????
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode() == status) {
                    //????????????????????????
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if (times > 3) {
                        log.info("??????:" + msgId + ",??????????????????3???,????????????????????????");
                        return;
                    }
                    mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    //??????????????????????????????
                    boolean b = tzMqConsumerMapper.updateConsumerTimes(mqConsumerLog);
                    if (!b) {
                        //???????????????,????????????????????????
                        log.info("????????????,????????????");
                        return;
                    }
                }

            } else {
                //4. ???????????????????????????...
                mqConsumerLog = new TzMqConsumer();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(0);

                //???????????????????????????????????????
                tzMqConsumerMapper.insert(mqConsumerLog);
            }
            //5. ????????????
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long goodsId = mqEntity.getGoodsId();
            TzGoods goods = tzGoodsDao.getById(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber() + mqEntity.getGoodsNumber());
            tzGoodsDao.updateById(goods);

            //????????????????????????
            TzGoodsLog goodsNumberLog = TzGoodsLog.builder().build();
            goodsNumberLog.setOrderId(String.valueOf(mqEntity.getOrderId()));
            goodsNumberLog.setGoodsId(Math.toIntExact(goodsId));
            goodsNumberLog.setGoodsNumber(mqEntity.getGoodsNumber());
            goodsNumberLog.setLogTime(LocalDateTime.now());
            tzGoodsLogDao.save(goodsNumberLog);

            //6. ????????????????????????????????????
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(LocalDateTime.now());
            tzMqConsumerMapper.updateById(mqConsumerLog);
            log.info("??????????????????");
        } catch (Exception e) {
            e.printStackTrace();
            LambdaQueryWrapper<TzMqConsumer> eq = new LambdaQueryWrapper<TzMqConsumer>()
                    .eq(TzMqConsumer::getMsgTag, tags).eq(TzMqConsumer::getMsgKey, keys).eq(TzMqConsumer::getGroupName, groupName);
            TzMqConsumer mqConsumerLog = tzMqConsumerMapper.selectOne(eq);
            if (mqConsumerLog == null) {
                //?????????????????????
                mqConsumerLog = new TzMqConsumer();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(1);
                tzMqConsumerMapper.insert(mqConsumerLog);
            } else {
                mqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
                tzMqConsumerMapper.updateById(mqConsumerLog);
            }
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

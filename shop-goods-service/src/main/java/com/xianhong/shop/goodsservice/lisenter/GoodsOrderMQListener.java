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
        log.info("库存服务收到消息:{}", JSON.toJSONString(messageExt));
        String tags = messageExt.getTags();
        log.info("库存服务消息tags:,{}", tags);

        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        String keys = messageExt.getKeys();
        String msgId = messageExt.getMsgId();


        switch (tags) {
            case "order_confirm": {
                MQEntity entity = JSON.parseObject(body, MQEntity.class);
                MqOrderCallbackEntity confirmGoods = confirmGoods(entity);
                try {
                    sendMessage(topic,callbackTag, String.valueOf(entity.getOrderId()),JSON.toJSONString(confirmGoods));
                    log.info("回调消息发送成功,{}",JSON.toJSONString(confirmGoods));
                } catch (Exception e) {
                    log.error("库存服务消费消息失败,{}",JSON.toJSONString(entity));
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
            //1. 解析消息内容

            log.info("接受消息成功");

            //2. 查询消息消费记录
            LambdaQueryWrapper<TzMqConsumer> eq = new LambdaQueryWrapper<TzMqConsumer>()
                    .eq(TzMqConsumer::getMsgTag, tags).eq(TzMqConsumer::getMsgKey, keys).eq(TzMqConsumer::getGroupName, groupName);
            TzMqConsumer mqConsumerLog = tzMqConsumerMapper.selectOne(eq);

            if (mqConsumerLog != null) {
                //3. 判断如果消费过...
                //3.1 获得消息处理状态
                Integer status = mqConsumerLog.getConsumerStatus();
                //处理过...返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode() == status) {
                    log.info("消息:" + msgId + ",已经处理过");
                    return;
                }

                //正在处理...返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode() == status) {
                    log.info("消息:" + msgId + ",正在处理");
                    return;
                }

                //处理失败
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode() == status) {
                    //获得消息处理次数
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if (times > 3) {
                        log.info("消息:" + msgId + ",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    //使用数据库乐观锁更新
                    boolean b = tzMqConsumerMapper.updateConsumerTimes(mqConsumerLog);
                    if (!b) {
                        //未修改成功,其他线程并发修改
                        log.info("并发修改,稍后处理");
                        return;
                    }
                }

            } else {
                //4. 判断如果没有消费过...
                mqConsumerLog = new TzMqConsumer();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(0);

                //将消息处理信息添加到数据库
                tzMqConsumerMapper.insert(mqConsumerLog);
            }
            //5. 回退库存
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long goodsId = mqEntity.getGoodsId();
            TzGoods goods = tzGoodsDao.getById(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber() + mqEntity.getGoodsNumber());
            tzGoodsDao.updateById(goods);

            //记录库存操作日志
            TzGoodsLog goodsNumberLog = TzGoodsLog.builder().build();
            goodsNumberLog.setOrderId(String.valueOf(mqEntity.getOrderId()));
            goodsNumberLog.setGoodsId(Math.toIntExact(goodsId));
            goodsNumberLog.setGoodsNumber(mqEntity.getGoodsNumber());
            goodsNumberLog.setLogTime(LocalDateTime.now());
            tzGoodsLogDao.save(goodsNumberLog);

            //6. 将消息的处理状态改为成功
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(LocalDateTime.now());
            tzMqConsumerMapper.updateById(mqConsumerLog);
            log.info("回退库存成功");
        } catch (Exception e) {
            e.printStackTrace();
            LambdaQueryWrapper<TzMqConsumer> eq = new LambdaQueryWrapper<TzMqConsumer>()
                    .eq(TzMqConsumer::getMsgTag, tags).eq(TzMqConsumer::getMsgKey, keys).eq(TzMqConsumer::getGroupName, groupName);
            TzMqConsumer mqConsumerLog = tzMqConsumerMapper.selectOne(eq);
            if (mqConsumerLog == null) {
                //数据库未有记录
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

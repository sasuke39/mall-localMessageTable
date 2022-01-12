package com.xianhong.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xianhong.shop.pojo.TzMqConsumer;
import org.apache.ibatis.annotations.Update;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
public interface TzMqConsumerMapper extends BaseMapper<TzMqConsumer> {

    @Update("update tz_mq_consumer set consumer_times=#{tzMqConsumer.consumerTimes}+1" +
            "where  msg_key = #{tzMqConsumer.consumerTimes} " +
            "and  msg_tag = #{tzMqConsumer.msgTag}" +
            "and  group_name = #{tzMqConsumer.groupName} " +
            "and  consumer_times=#{tzMqConsumer.consumerTimes}")
    public boolean updateConsumerTimes(TzMqConsumer tzMqConsumer);

}

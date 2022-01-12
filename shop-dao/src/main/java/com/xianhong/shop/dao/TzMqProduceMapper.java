package com.xianhong.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xianhong.shop.pojo.TzMqProduce;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
public interface TzMqProduceMapper extends BaseMapper<TzMqProduce> {

    @Update("update tz_mq_produce set consume_times = consume_times+1, ext = #{tzPro.ext} ,msg_status = #{tzPro.msgStatus} where consume_times = #{tzPro.consumeTimes} and id = #{tzPro.id}")
    public boolean updateProByLuckyLock(@Param("tzPro") TzMqProduce tzPro);

    @Select("select * from tz_mq_produce where msg_key =#{msgKeys} for update")
    public TzMqProduce getByIdForUpdate(String msgKeys);

    @Select("select * from tz_mq_produce where msg_key =#{msgKeys}")
    public TzMqProduce getById(String msgKeys);
}

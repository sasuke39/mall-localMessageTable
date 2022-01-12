package com.xianhong.shop.goodsservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.pojo.TzGoods;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-27
 */
public interface TzGoodsDao extends IService<TzGoods> {
    public boolean confirmGoods(Long userId, TzGoods goods, Long orderId);


    public MqOrderCallbackEntity confirmGoodsNew(Long userId, TzGoods goods, Long orderId);
}

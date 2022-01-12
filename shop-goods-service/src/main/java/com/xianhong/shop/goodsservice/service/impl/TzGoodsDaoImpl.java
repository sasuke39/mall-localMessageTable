package com.xianhong.shop.goodsservice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.dao.TzGoodsMapper;
import com.xianhong.shop.goodsservice.service.TzGoodsDao;
import com.xianhong.shop.goodsservice.service.TzGoodsLogDao;
import com.xianhong.shop.pojo.TzGoods;
import com.xianhong.shop.pojo.TzGoodsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-27
 */
@Service
public class TzGoodsDaoImpl extends ServiceImpl<TzGoodsMapper, TzGoods> implements TzGoodsDao {

    @Resource
    private TzGoodsLogDao tzGoodsLogDao;
    @Override
    @Transactional
    public boolean confirmGoods(Long userId, TzGoods goods, Long orderId) {
        Long goodsId = goods.getGoodsId();
        TzGoods tzGoods = this.getById(goodsId);
        Integer goodsNumber = goods.getGoodsNumber();
        tzGoods.setGoodsNumber(tzGoods.getGoodsNumber()- goodsNumber);
        tzGoods.setUpdateTime(LocalDateTime.now());
        TzGoodsLog goodsLog = TzGoodsLog.builder().goodsId(Math.toIntExact(goodsId)).goodsNumber(goodsNumber).logTime(LocalDateTime.now()).orderId(String.valueOf(orderId)).build();
        this.saveOrUpdate(tzGoods);
        tzGoodsLogDao.save(goodsLog);
        return true;
    }


    @Override
    @Transactional
    public MqOrderCallbackEntity confirmGoodsNew(Long userId, TzGoods goods, Long orderId) {
        Long goodsId = goods.getGoodsId();
        TzGoods tzGoods = this.getById(goodsId);
        Integer goodsNumber = goods.getGoodsNumber();
        tzGoods.setGoodsNumber(tzGoods.getGoodsNumber()- goodsNumber);
        tzGoods.setUpdateTime(LocalDateTime.now());
        TzGoodsLog goodsLog = TzGoodsLog.builder().goodsId(Math.toIntExact(goodsId)).goodsNumber(goodsNumber).logTime(LocalDateTime.now()).orderId(String.valueOf(orderId)).build();
        this.saveOrUpdate(tzGoods);
        tzGoodsLogDao.save(goodsLog);
        return MqOrderCallbackEntity.builder().goodsId(goodsId).goodsLogId(Long.valueOf(goodsLog.getGoodsLogId())).build();
    }
}

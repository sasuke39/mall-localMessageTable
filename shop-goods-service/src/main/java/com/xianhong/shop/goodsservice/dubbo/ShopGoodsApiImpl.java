package com.xianhong.shop.goodsservice.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.xianhong.shop.api.goods.ShopGoodsApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.goodsservice.service.TzGoodsDao;
import com.xianhong.shop.goodsservice.service.TzGoodsLogDao;
import com.xianhong.shop.pojo.TzGoods;
import com.xianhong.shop.pojo.TzGoodsLog;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@Service
@Component
public class ShopGoodsApiImpl implements ShopGoodsApi {
    @Resource
    private TzGoodsDao tzGoodsDao;
    @Resource
    private TzGoodsLogDao tzGoodsLogDao;
    @Override
    public FinalResponse<Boolean> goodsConfirm(Long userId, TzGoods goods, Long orderId) {
        return FinalResponse.buildSuccess(tzGoodsDao.confirmGoods(userId,goods,orderId));
    }

    @Override
    public FinalResponse<Boolean> reduceGoodsNum(TzGoodsLog goodsNumberLog) {
        try{
        if (goodsNumberLog == null ||
                goodsNumberLog.getGoodsNumber() == null ||
                goodsNumberLog.getOrderId() == null ||
                goodsNumberLog.getGoodsNumber() <= 0) {
            throw new ShopException("SHOP_REQUEST_PARAMETER_VALID");
        }
        TzGoods goods = tzGoodsDao.getById(goodsNumberLog.getGoodsId());
        if(goods.getGoodsNumber()<goodsNumberLog.getGoodsNumber()){
            //库存不足
            throw new ShopException("SHOP_GOODS_NUM_NOT_ENOUGH");
        }
        //减库存
        goods.setGoodsNumber(goods.getGoodsNumber()-goodsNumberLog.getGoodsNumber());
        tzGoodsDao.updateById(goods);

        //记录库存操作日志
        goodsNumberLog.setGoodsNumber(-(goodsNumberLog.getGoodsNumber()));
        goodsNumberLog.setLogTime(LocalDateTime.now());
        tzGoodsLogDao.save(goodsNumberLog);

        }catch (ShopException shopException){
            return FinalResponse.buildFail(shopException.getMessage());
        } catch (Exception e){
            return FinalResponse.buildFail("failed");
        }
        return FinalResponse.buildSuccess(true);
    }
}

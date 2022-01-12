package com.xianhong.shop.api.goods;

import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.pojo.TzGoods;
import com.xianhong.shop.pojo.TzGoodsLog;
import com.xianhong.shop.pojo.TzOrder;

/**
 * @author xianhong
 * @date 2022/1/4
 */
public interface ShopGoodsApi {
    public FinalResponse<Boolean> goodsConfirm(Long userId, TzGoods goods,Long orderId);

    public FinalResponse<Boolean> reduceGoodsNum(TzGoodsLog tzGoodsLog);
}

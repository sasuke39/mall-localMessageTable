package com.xianhong.shop.api.order;


import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.pojo.TzOrder;
import com.xianhong.shop.req.OrderRequest;
import com.xianhong.shop.res.OrderResponse;

import java.util.List;

/**
 * @author xianhong
 * @date 2021/12/28
 */
public interface ShopOrderApi {
    public OrderResponse confirmOrder(OrderRequest request);

    public OrderResponse insertOrder(TzOrder order);

    public FinalResponse<Boolean> updateOrder(TzOrder tzOrder);

}

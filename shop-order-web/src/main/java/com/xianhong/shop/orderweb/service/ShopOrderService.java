package com.xianhong.shop.orderweb.service;

import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.req.OrderRequest;
import com.xianhong.shop.res.OrderResponse;

/**
 * @author xianhong
 * @date 2022/1/4
 */
public interface ShopOrderService {
    public OrderResponse confirmOrder(OrderRequest order) throws ShopException ;

    public OrderResponse confirmOrderByNativeTable(OrderRequest order) throws  Exception;
}

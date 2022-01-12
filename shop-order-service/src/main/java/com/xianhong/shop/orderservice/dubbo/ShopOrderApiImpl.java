package com.xianhong.shop.orderservice.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.xianhong.shop.api.coupon.ShopCouponApi;
import com.xianhong.shop.api.goods.ShopGoodsApi;
import com.xianhong.shop.api.order.ShopOrderApi;
import com.xianhong.shop.api.user.ShopUserApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.orderservice.service.TzOrderDao;
import com.xianhong.shop.pojo.TzOrder;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.req.OrderRequest;
import com.xianhong.shop.res.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@Component
@Service(interfaceClass = ShopOrderApi.class)
@Slf4j
public class ShopOrderApiImpl implements ShopOrderApi {

    @Resource
    TzOrderDao tzOrderDao;

    @Override
    public OrderResponse confirmOrder(OrderRequest request) {
//        boolean b = tzOrderDao.confirmOrder();
        return OrderResponse.builder().orderId(1L).build();
    }


    @Override
    public OrderResponse insertOrder(TzOrder order) {
        tzOrderDao.save(order);
        return OrderResponse.builder().orderId(order.getOrderId()).build();
    }

    @Override
    public FinalResponse<Boolean> updateOrder(TzOrder tzOrder) {
        return FinalResponse.buildSuccess(tzOrderDao.updateById(tzOrder));
    }
}

package com.xianhong.shop.demo;

import com.alibaba.fastjson.JSON;
import com.xianhong.shop.common.mq.MqOrderCallbackEntity;
import com.xianhong.shop.orderweb.ShopOrderWeb;
import com.xianhong.shop.orderweb.service.ShopOrderService;
import com.xianhong.shop.pojo.TzOrder;
import com.xianhong.shop.req.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopOrderWeb.class)
class ShopOrderServiceTests {

    @Resource
    private ShopOrderService orderService;

    @Test
    public void add(){
        Long goodsId=1L;
        Long userId=1L;
        Long couponId=2L;

        TzOrder order = new TzOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setGoodsNumber(1);
        order.setAddress("北京");
        order.setGoodsPrice(new BigDecimal("5000"));
        order.setOrderAmount(new BigDecimal("5000"));
        order.setMoneyPaid(new BigDecimal("100"));
        order.setCouponId(couponId);
        order.setShippingFee(new BigDecimal(0));
        OrderRequest build = OrderRequest.builder().order(order).build();
        orderService.confirmOrder(build);
    }

    public static void main(String[] args) {
        MqOrderCallbackEntity build = MqOrderCallbackEntity.builder().build();
        System.out.println(JSON.toJSONString(build));
    }

}

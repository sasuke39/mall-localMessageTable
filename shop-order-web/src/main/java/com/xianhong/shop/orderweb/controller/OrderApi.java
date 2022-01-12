package com.xianhong.shop.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xianhong.shop.api.coupon.ShopCouponApi;
import com.xianhong.shop.api.goods.ShopGoodsApi;
import com.xianhong.shop.api.order.ShopOrderApi;
import com.xianhong.shop.api.user.ShopUserApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.orderweb.service.ShopOrderService;
import com.xianhong.shop.pojo.TzOrder;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.req.OrderRequest;
import com.xianhong.shop.res.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@RestController
@RequestMapping("/shopOrderApiProvider")
@Slf4j
public class OrderApi {

    @Reference
    ShopOrderApi shopOrderApi;
    @Resource
    private ShopOrderService shopOrderService;
    @Reference
    ShopUserApi shopUserApi;

    @PostMapping ("/confirm")
    public FinalResponse<OrderResponse> confirmOrder(@RequestBody OrderRequest request){
        OrderResponse response;
        try {
            response = shopOrderService.confirmOrderByNativeTable(request);
        }catch (Exception exception){
            log.error("fail order confirm,{}",exception.getMessage());
            return FinalResponse.buildFail(exception.getMessage());
        }
        return FinalResponse.buildSuccess(response);
    }

//    @PostMapping("/insert")
//    public FinalResponse<OrderResponse> insert(@RequestBody List<TzOrder> tzOrders){
//        return FinalResponse.buildSuccess(shopOrderApi.insertOrder(tzOrders));
//    }
    @GetMapping("/test")
    public String test(){
        return "ssss";
    }

    @GetMapping("/getUser")
    public FinalResponse<TzUser> getUser(Long id){
        return shopUserApi.getUserInfoByUserId(id);
    }


}

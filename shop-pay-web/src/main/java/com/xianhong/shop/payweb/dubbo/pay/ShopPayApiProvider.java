package com.xianhong.shop.payweb.dubbo.pay;

import com.xianhong.shop.api.pay.ShopPayApi;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@RestController
@RequestMapping("/shopPayApiProvider")
public class ShopPayApiProvider {
    @Reference
    ShopPayApi shopPayApi;

//    @GetMapping("/pay")
//    public String pay(){
//        return shopPayApi();
//    }
}

package com.xianhong.shop.couponservice;


import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan("com.xianhong.shop.dao")
public class ShopCouponService {

    public static void main(String[] args) {
        SpringApplication.run(ShopCouponService.class, args);
    }

}

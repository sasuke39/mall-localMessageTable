package com.xianhong.shop.orderservice;


import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan("com.xianhong.shop.dao")
public class ShopOrderService {

    public static void main(String[] args) {
        SpringApplication.run(ShopOrderService.class, args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(ShopOrderService.class);
//    }
}

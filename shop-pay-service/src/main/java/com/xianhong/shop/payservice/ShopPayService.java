package com.xianhong.shop.payservice;


import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
public class ShopPayService {

    public static void main(String[] args) {
        SpringApplication.run(ShopPayService.class, args);
    }

}

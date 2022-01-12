package com.xianhong.shop.userservice;


import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan("com.xianhong.shop.dao")
public class ShopUserService {

    public static void main(String[] args) {
        SpringApplication.run(ShopUserService.class, args);
    }

}

package com.xianhong.shop.couponservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@SpringBootTest
class ShopCouponServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        double anInt = Double.parseDouble("24");
        anInt= anInt/ 100;
        BigDecimal one  = new BigDecimal("23.55");
        BigDecimal two  = new BigDecimal(anInt);
        BigDecimal value = one.multiply(two);
        value = value.setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal decimal = value.add(one);
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
//        double finalPrice = one.add(value).doubleValue();
//        System.out.println(x*num2);
        String format = decimalFormat.format(decimal);
        System.out.println(format);
        double test = 29.13;

        BigDecimal teszt  = new BigDecimal("9.347");
        teszt = teszt.setScale(2,BigDecimal.ROUND_HALF_UP);
        System.out.println(teszt.doubleValue());
    }

}

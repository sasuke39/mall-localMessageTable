package com.xianhong.shop.message;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xianhong
 * @date 2022/1/8
 */
@Data
public class ConfirmOrderMQ {

    Long orderId;

    Long couponId;

    Long goodsId;

    Integer goodsNumber;

    Long userId;

    BigDecimal userMoney;
}

package com.xianhong.shop.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xianhong
 * @date 2022/1/5
 */
@Data
//@Builder
//@AllArgsConstructor
public class CancelOrderMQ {

    Long orderId;

    Long couponId;

    Long goodsId;

    Integer goodsNumber;

    Long userId;

    BigDecimal userMoney;



}

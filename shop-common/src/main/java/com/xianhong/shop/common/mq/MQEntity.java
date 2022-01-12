package com.xianhong.shop.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xianhong
 * @date 2022/1/5
 */
@Data
@AllArgsConstructor
@Builder
public class MQEntity {
    Long goodsId;

    Integer goodsNumber;

    Long orderId;

    Long couponId;

    Long userId;

    BigDecimal userMoney;


}

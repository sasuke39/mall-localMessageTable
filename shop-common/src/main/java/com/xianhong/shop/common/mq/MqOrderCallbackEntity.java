package com.xianhong.shop.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author xianhong
 * @date 2022/1/11
 */
@Builder
@AllArgsConstructor
@Data
public class MqOrderCallbackEntity {
    Long orderId;

    Long couponId;

    Long userId;

    Long goodsId;

    Long goodsLogId;

}

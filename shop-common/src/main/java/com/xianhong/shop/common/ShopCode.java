package com.xianhong.shop.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@AllArgsConstructor
@Getter
public enum ShopCode{
    //订单状态 0未确认 1已确认 2已取消 3无效 4退款
    SHOP_ORDER_NO_CONFIRM(0),
    SHOP_ORDER_CONFIRMED(1),
    SHOP_ORDER_CANCELED(2),
    SHOP_ORDER_DISABLE(3),
    SHOP_ORDER_REFUND(4),

    //是否使用 0未使用 1已使用
    SHOP_COUPON_NO_EXIST(-1),
    SHOP_COUPON_NO_USE(0),
    SHOP_COUPON_USED(1),

    //日志类型 1订单付款 2 订单退款
    SHOP_USER_MONEY_PAID(1),
    SHOP_USER_MONEY_REFUND(2),

    //支付状态 0未支付 1支付中 2已支付
    SHOP_ORDER_UN_PAY(0),
    SHOP_ORDER_PAYING(1),
    SHOP_ORDER_PAYED(2),

    //0:正在处理;1:处理成功;2:处理失败
    SHOP_MQ_MESSAGE_STATUS_PROCESSING(0),
    SHOP_MQ_MESSAGE_STATUS_SUCCESS(1),
    SHOP_MQ_MESSAGE_STATUS_FAIL(2),
    ;

    int code;


}

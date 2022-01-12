package com.xianhong.shop.api.coupon;

import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.pojo.TzCoupon;
import com.xianhong.shop.pojo.TzOrder;

/**
 * @author xianhong
 * @date 2022/1/4
 */
public interface ShopCouponApi {
    public FinalResponse<Boolean> reduceCoupon(TzCoupon tzCoupon);

    public FinalResponse<TzCoupon> getCouponById(Long couponId);
}

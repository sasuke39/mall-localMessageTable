package com.xianhong.shop.couponservice.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.xianhong.shop.api.coupon.ShopCouponApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.couponservice.service.TzCouponDao;
import com.xianhong.shop.pojo.TzCoupon;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@Service
@Component
public class ShopCouponApiImpl implements ShopCouponApi {
    @Resource
    TzCouponDao tzCouponDao;

    @Override
    public FinalResponse<Boolean> reduceCoupon(TzCoupon tzCoupon) {
        try {
            //判断请求参数是否合法
            if (tzCoupon == null || StringUtils.isEmpty(tzCoupon.getCouponId())) {
                throw new ShopException("SHOP_REQUEST_PARAMETER_VALID");
            }
            //更新优惠券状态为已使用
            boolean updateById = tzCouponDao.updateById(tzCoupon);
            return FinalResponse.buildSuccess(updateById);
        } catch (Exception e) {
            return FinalResponse.buildFail("failed");
        }
    }

    @Override
    public FinalResponse<TzCoupon> getCouponById(Long couponId) {
        return FinalResponse.buildSuccess(tzCouponDao.getById(couponId));
    }
}

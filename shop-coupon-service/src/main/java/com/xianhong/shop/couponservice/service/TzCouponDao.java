package com.xianhong.shop.couponservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xianhong.shop.pojo.TzCoupon;
import com.xianhong.shop.pojo.TzOrder;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
public interface TzCouponDao extends IService<TzCoupon> {

    void confirmOrder(TzOrder tzorder);
}

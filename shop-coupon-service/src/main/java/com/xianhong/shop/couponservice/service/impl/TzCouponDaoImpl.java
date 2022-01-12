package com.xianhong.shop.couponservice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.couponservice.service.TzCouponDao;
import com.xianhong.shop.dao.TzCouponMapper;
import com.xianhong.shop.pojo.TzCoupon;
import com.xianhong.shop.pojo.TzOrder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Service
public class TzCouponDaoImpl extends ServiceImpl<TzCouponMapper, TzCoupon> implements TzCouponDao {

    @Override
    public void confirmOrder(TzOrder tzorder) {

    }
}

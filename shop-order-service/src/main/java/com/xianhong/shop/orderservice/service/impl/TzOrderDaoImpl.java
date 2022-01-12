package com.xianhong.shop.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.dao.TzOrderMapper;
import com.xianhong.shop.orderservice.service.TzOrderDao;
import com.xianhong.shop.pojo.TzOrder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Service
public class TzOrderDaoImpl extends ServiceImpl<TzOrderMapper, TzOrder> implements TzOrderDao {
//    @Override
//    public boolean confirmOrder() {
//        return this.updateById();
//    }
}

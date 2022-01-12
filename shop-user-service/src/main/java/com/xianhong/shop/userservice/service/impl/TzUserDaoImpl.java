package com.xianhong.shop.userservice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.dao.TzUserMapper;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.userservice.service.TzUserDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Service
public class TzUserDaoImpl extends ServiceImpl<TzUserMapper, TzUser> implements TzUserDao {

    @Override
    public void reduceUserMoney(TzUser tzUser) {
        TzUser user = this.getById(tzUser.getUserId());
        if (user==null){
            throw new ShopException("用户不存在");
        }
        BigDecimal userMoney = user.getUserMoney();
        BigDecimal userMoney1 = tzUser.getUserMoney();
        if (userMoney.doubleValue()<userMoney1.doubleValue()){
            throw new ShopException("余额不足");
        }
        BigDecimal divide = userMoney.subtract(userMoney1);
        user.setUserMoney(divide);
        this.updateById(user);

    }

    @Override
    public void addUserMoney(TzUser tzUser) {
        TzUser user = this.getById(tzUser.getUserId());
        if (user==null){
            throw new ShopException("用户不存在");
        }
        BigDecimal userMoney = user.getUserMoney();
        BigDecimal userMoney1 = tzUser.getUserMoney();
        BigDecimal divide = userMoney.add(userMoney1);
        user.setUserMoney(divide);
        this.updateById(user);
    }
}

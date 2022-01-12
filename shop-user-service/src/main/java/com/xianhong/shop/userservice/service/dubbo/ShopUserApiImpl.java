package com.xianhong.shop.userservice.service.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.xianhong.shop.api.user.ShopUserApi;
import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.pojo.TzBalanceLog;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.userservice.service.TzBalanceLogDao;
import com.xianhong.shop.userservice.service.TzUserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@Service
@Component
public class ShopUserApiImpl implements ShopUserApi {
    @Resource
    private TzUserDao tzUserDao;
    @Resource
    private TzBalanceLogDao tzBalanceLogDao;


    @Override
    public FinalResponse<TzUser> getUserInfoByUserId(Long userId) {
        return FinalResponse.buildSuccess(tzUserDao.getById(userId));
    }

    @Override
    public FinalResponse<Boolean> changeUserMoney(TzBalanceLog tzBalanceLog) {
        tzBalanceLogDao.changeUserMoney(tzBalanceLog);
        return FinalResponse.buildSuccess(true);
    }
}

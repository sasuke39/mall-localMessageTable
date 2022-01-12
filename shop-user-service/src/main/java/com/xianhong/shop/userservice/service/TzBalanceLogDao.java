package com.xianhong.shop.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xianhong.shop.pojo.TzBalanceLog;

/**
 * @author xianhong
 * @date 2022/1/4
 */
public interface TzBalanceLogDao extends IService<TzBalanceLog> {
    void changeUserMoney(TzBalanceLog tzBalanceLog);
}

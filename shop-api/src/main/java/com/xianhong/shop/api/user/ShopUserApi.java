package com.xianhong.shop.api.user;

import com.xianhong.shop.common.FinalResponse;
import com.xianhong.shop.pojo.TzBalanceLog;
import com.xianhong.shop.pojo.TzUser;

/**
 * @author xianhong
 * @date 2022/1/4
 */
public interface ShopUserApi {
    FinalResponse<TzUser> getUserInfoByUserId(Long userId);
    FinalResponse<Boolean> changeUserMoney(TzBalanceLog tzBalanceLog);
}

package com.xianhong.shop.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xianhong.shop.pojo.TzUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
public interface TzUserDao extends IService<TzUser> {
    public void reduceUserMoney(TzUser tzUser);
    public void addUserMoney(TzUser tzUser);
}

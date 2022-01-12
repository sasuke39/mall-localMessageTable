package com.xianhong.shop.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xianhong.shop.common.ShopCode;
import com.xianhong.shop.common.exception.ShopException;
import com.xianhong.shop.dao.TzBalanceLogMapper;
import com.xianhong.shop.dao.TzUserMapper;
import com.xianhong.shop.pojo.TzBalanceLog;
import com.xianhong.shop.pojo.TzUser;
import com.xianhong.shop.userservice.service.TzBalanceLogDao;

import com.xianhong.shop.userservice.service.TzUserDao;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * @author xianhong
 * @date 2022/1/4
 */
@Service
public class TzBalanceLogDaoImpl extends ServiceImpl<TzBalanceLogMapper, TzBalanceLog> implements TzBalanceLogDao {

    @Resource
    private TzUserDao tzUserDao;

    @Override
    public void changeUserMoney(TzBalanceLog userMoneyLog) {
        //判断请求参数是否合法
        if (userMoneyLog == null
                || userMoneyLog.getUserId() == null
                || userMoneyLog.getUseMoney() == null
                || userMoneyLog.getOrderId() == null
                || userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ShopException("SHOP_REQUEST_PARAMETER_VALID");
        }

        //查询该订单是否存在付款记录
        LambdaQueryWrapper<TzBalanceLog> queryWrapper = new LambdaQueryWrapper<TzBalanceLog>().eq(TzBalanceLog::getUserId, userMoneyLog.getUserId()).eq(TzBalanceLog::getOrderId, userMoneyLog.getOrderId());
        TzBalanceLog one = this.getOne(queryWrapper);
        TzUser tradeUser = new TzUser();
        tradeUser.setUserId(userMoneyLog.getUserId());
        tradeUser.setUserMoney(userMoneyLog.getUseMoney());
        //判断余额操作行为
        //【付款操作】
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_PAID.getCode())) {
            //订单已经付款，则抛异常
            if (one!=null) {
                throw new ShopException("SHOP_ORDER_PAY_STATUS_IS_PAY");
            }
            //用户账户扣减余额
            tzUserDao.reduceUserMoney(tradeUser);
        }
        //【退款操作】
        if (userMoneyLog.getMoneyLogType().equals(ShopCode.SHOP_USER_MONEY_REFUND.getCode())) {
            //如果订单未付款,则不能退款,抛异常
            if (one==null) {
                throw new ShopException("SHOP_ORDER_PAY_STATUS_UN_PAY");
            }
            //防止多次退款
            queryWrapper.eq(TzBalanceLog::getMoneyLogType,ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            TzBalanceLog balanceLog = this.getOne(queryWrapper);
            if (balanceLog!=null) {
                throw new ShopException("SHOP_ORDER_PAY_STATUS_REFUND");
            }
            //用户账户添加余额
            tzUserDao.addUserMoney(tradeUser);
        }


        //记录用户使用余额日志
        userMoneyLog.setCreateTime(LocalDateTime.now());
        this.save(userMoneyLog);
    }


}

package com.xianhong.shop.pojo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tz_balance_log")
public class TzBalanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer balanceLogId;

    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 日志类型 1订单付款 2 订单退款
     */
    private Integer moneyLogType;

    /**
     * 操作金额
     */
    private BigDecimal useMoney;

    private LocalDateTime createTime;


}

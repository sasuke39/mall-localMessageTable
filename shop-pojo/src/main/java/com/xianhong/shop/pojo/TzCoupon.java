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
@TableName("tz_coupon")
public class TzCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID	|          
     */
    @TableId(type = IdType.AUTO)
    private Long couponId;

    /**
     * 优惠券金额
     */
    private BigDecimal couponPrice;

    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 是否使用 0未使用 1已使用
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;


}

package com.xianhong.shop.pojo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tz_pay")
public class TzPay implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long payId;

    /**
     * 订单编号
     */
    private Long orderId;

    private BigDecimal payAmount;

    /**
     * 是否已支付 1否 2是
     */
    private Integer isPaid;


}

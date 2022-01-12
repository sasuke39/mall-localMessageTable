package com.xianhong.shop.pojo;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cglib.core.Local;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tz_order")
public class TzOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态 0未确认 1已确认 2已取消 3无效 4退款
     */
    private Integer orderStatus;

    /**
     * 支付状态 0未支付 1支付中 2已支付
     */
    private Integer payStatus;

    /**
     * 发货状态 0未发货 1已发货 2已退货
     */
    private Integer shippingStatus;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品数量
     */
    private Integer goodsNumber;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品总价
     */
    private BigDecimal goodsAmount;

    /**
     * 订单价格
     */
    private BigDecimal orderAmount;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券
     */
    private BigDecimal couponPaid;

    /**
     * 已付金额
     */
    private BigDecimal moneyPaid;

    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 创建时间
     */
//    @JSONField( format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addTime;

    /**
     * 订单确认时间
     */
//    @JSONField( format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    /**
     * 支付时间
     */
//    @JSONField( format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0正常 1删除
     */
    private Boolean isDeleted;


}

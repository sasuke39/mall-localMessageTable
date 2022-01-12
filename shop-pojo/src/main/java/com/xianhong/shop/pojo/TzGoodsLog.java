package com.xianhong.shop.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单日志表
 * </p>
 *
 * @author xianhong.zhou
 * @since 2021-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("tz_goods_log")
public class TzGoodsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单日志id
     */
    @TableId(value = "goods_log_id", type = IdType.AUTO)
    private Integer goodsLogId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 库存数量
     */
    private Integer goodsNumber;

    /**
     * 记录时间
     */
    private LocalDateTime logTime;


}

package com.xianhong.shop.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Builder;
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
@TableName("tz_mq_produce")
@Builder
public class TzMqProduce implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 生产者组名
     */
    private String groupName;

    private String msgTopic;

    private String msgTag;

    private String msgKey;

    private String msgBody;

    /**
     * 0:未处理;1:已经处理
//     * @see com.xianhong.shop.common.ShopCode
     */
    private Integer msgStatus;

    private LocalDateTime createTime;

    /**
     * 扩展字段
     */
    private String ext;

    private Integer consumeTimes;


}

package com.xianhong.shop.pojo;

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
@TableName("tz_mq_consumer")
public class TzMqConsumer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String msgId;

    private String groupName;

    private String msgTag;

    private String msgKey;

    private String msgBody;

    /**
     * 0:正在处理;1:处理成功;2:处理失败
     */
    private Integer consumerStatus;

    /**
     * 消费次数
     */
    private Integer consumerTimes;

    private LocalDateTime consumerTimestamp;

    private String remark;


}

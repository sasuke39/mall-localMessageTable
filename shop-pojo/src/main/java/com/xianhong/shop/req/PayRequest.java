package com.xianhong.shop.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@Data
@Builder
@AllArgsConstructor
public class PayRequest implements Serializable {
    private static final long serialVersionUID = 9004459815483909836L;
    Integer userId;
    String payAmount;
}

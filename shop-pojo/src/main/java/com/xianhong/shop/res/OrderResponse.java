package com.xianhong.shop.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse implements Serializable {
    private static final long serialVersionUID = -2148415158103567845L;
    Long orderId;
}

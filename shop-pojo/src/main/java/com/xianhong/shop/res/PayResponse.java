package com.xianhong.shop.res;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@Data
@AllArgsConstructor
@Builder
public class PayResponse implements Serializable {
   private static final long serialVersionUID = 6036125246165287444L;
   boolean isSuccess;
}

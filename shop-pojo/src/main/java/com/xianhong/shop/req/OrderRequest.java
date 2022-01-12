package com.xianhong.shop.req;

import com.xianhong.shop.pojo.TzOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author xianhong
 * @date 2021/12/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest implements Serializable {

    private static final long serialVersionUID = 1756680191537283781L;
    private TzOrder order;
}

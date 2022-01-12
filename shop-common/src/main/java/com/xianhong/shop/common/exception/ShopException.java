package com.xianhong.shop.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;


/**
 * @author xianhong
 * @date 2022/1/4
 */
public class ShopException extends RuntimeException{
    private static final long serialVersionUID = 631164703814501069L;

    public ShopException(String msg){
        super(msg);
    }
}

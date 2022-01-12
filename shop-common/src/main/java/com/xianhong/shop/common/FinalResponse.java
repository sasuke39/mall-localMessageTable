package com.xianhong.shop.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author xianhong
 * @date 2021/12/30
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalResponse<T> implements Serializable {
    private static final long serialVersionUID = 51706508934658917L;
    public static int SUCCESS = 200;
    public static int FAIL = 500;
    Integer code;
    String msg;
    T data;

    public static <T> FinalResponse<T> buildSuccess(T param){
        return (FinalResponse<T>) FinalResponse.builder().code(SUCCESS).data(param).build();
    }

    public static <T> FinalResponse<T> buildFail(String msg){
        return (FinalResponse<T>) FinalResponse.builder().code(FAIL).msg(msg).build();
    }
}

package com.atguigu.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201,"失败");
    private ResultCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    private Integer code;
    private String message;


}

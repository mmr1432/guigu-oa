package com.atguigu.common.result;

import lombok.Data;

@Data
public class Result<T>{
    private Integer code;
    private String message;
    private T data;
    public Result(){}
    public static<T> Result<T> build(T data){
        Result<T> tResult = new Result<>();
        if (data != null){
            tResult.setData(data);
        }
        return tResult;
    }
    public static<T> Result<T> build(Integer code,String message,T data){
        Result<T> tResult = build(data);
        tResult.setCode(code);
        tResult.setMessage(message);
        return tResult;
    }

    public static<T> Result<T> build(T data,ResultCodeEnum resultCodeEnum){
        Result<T> tResult = build(data);
        tResult.setCode(resultCodeEnum.getCode());
        tResult.setMessage(resultCodeEnum.getMessage());
        return tResult;
    }

    public static<T> Result<T> success(T data){
        Result<T> tResult = build(data);
        tResult.setCode(ResultCodeEnum.SUCCESS.getCode());
        tResult.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return tResult;
    }
    public static<T> Result<T> fail(T data){
        Result<T> tResult = build(data);
        tResult.setCode(ResultCodeEnum.FAIL.getCode());
        tResult.setMessage(ResultCodeEnum.FAIL.getMessage());
        return tResult;
    }
}

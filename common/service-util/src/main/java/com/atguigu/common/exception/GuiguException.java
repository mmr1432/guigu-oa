package com.atguigu.common.exception;


import com.atguigu.common.result.ResultCodeEnum;
import lombok.Data;



@Data
public class GuiguException extends RuntimeException{
    private Integer code;
    private String msg;//错误信息

    public GuiguException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public GuiguException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }
    /**
     * spring security异常
     * @param e
     * @return
     */

}

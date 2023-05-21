package com.rysiw.chatgptmanage.common.exception;

import com.rysiw.chatgptmanage.common.enums.RespCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class DefineException extends RuntimeException{

    protected String errorCode;
    protected Object data;

    public DefineException(){

    }

    public DefineException(String errorCode, String errorMsg){
        super(errorMsg);
        this.errorCode = errorCode;
    }

    public DefineException(RespCode respCode){
        super(respCode.getMsg());
        this.errorCode = respCode.getCode();
    }

    public DefineException(RespCode respCode, Object data){
        super(respCode.getMsg());
        this.errorCode = respCode.getCode();
        //面向内部调试用的exception, 统一处理
        if(respCode.equals(RespCode.INTERNAL_ERROR)){
            this.data = "";
            log.error(data.toString());
        }
        else
            this.data = data;
    }

    public DefineException(Object data){
        super(((Exception)data).getMessage());
        log.error(((Exception) data).getMessage());
        this.errorCode = RespCode.ERROR.getCode();
        this.data = RespCode.ERROR.getMsg();
    }
}

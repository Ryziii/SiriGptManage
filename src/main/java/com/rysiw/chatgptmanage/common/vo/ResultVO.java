package com.rysiw.chatgptmanage.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVO<T> {
    private String code;
    private String msg;
    private T data;

    public ResultVO(){

    }
    public ResultVO(String code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO<T> buildSuccess(){
        this.code = RespCode.SUCCESS.getCode();
        this.msg = RespCode.SUCCESS.getMsg();
        return this;
    }

    public static ResultVO buildError(){
        ResultVO resultVO = new ResultVO();
        resultVO.code = RespCode.ERROR.getCode();
        resultVO.msg = RespCode.ERROR.getMsg();
        return resultVO;
    }

    public static ResultVO buildDefineError(DefineException e){
        ResultVO resultVO = new ResultVO();
        resultVO.code = e.getErrorCode();
        resultVO.msg = e.getMessage();
        resultVO.data = e.getData();
        return resultVO;
    }

    public static ResultVO otherError(Exception e) {
        ResultVO resultVO = new ResultVO();
        resultVO.code = RespCode.ERROR.getCode();
        resultVO.msg = e.getMessage();
        log.error(e.getMessage(), e);
        return resultVO;
    }

    public static ResultVO<Object> getSuccessVO(){
        return ResultVO.builder().code(RespCode.SUCCESS.getCode()).msg(RespCode.SUCCESS.getMsg()).build();
    }

    public static ResultVO<Object> getSuccessVO(String msg){
        return ResultVO.builder().code(RespCode.SUCCESS.getCode()).msg(msg).build();
    }

    public static <T> ResultVO<T> buildSuccess(T data){
        return new ResultVO(RespCode.SUCCESS.getCode(), RespCode.SUCCESS.getMsg(), data);
    }

    public static ResultVO<Object> getErrorVO() {
        return ResultVO.builder().code(RespCode.ERROR.getCode()).msg(RespCode.ERROR.getMsg()).build();
    }

    public static ResultVO<Object> buildError(String msg) {
        return ResultVO.builder().code(RespCode.ERROR.getCode()).msg(msg).build();
    }
}

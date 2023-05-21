package com.rysiw.chatgptmanage.common.exception;

import com.rysiw.chatgptmanage.common.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = DefineException.class)
    @ResponseBody
    public ResultVO bizException(DefineException e){
        return ResultVO.buildDefineError(e);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO exceptionHandle(Exception e){
        return ResultVO.otherError(e);
    }
}

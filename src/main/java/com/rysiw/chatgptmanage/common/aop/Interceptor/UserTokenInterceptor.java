package com.rysiw.chatgptmanage.common.aop.Interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rysiw.chatgptmanage.common.enums.CommonConstant;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import com.rysiw.chatgptmanage.common.vo.ResultVO;
import com.rysiw.chatgptmanage.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Header 中获取 userToken
        String userToken = request.getHeader("userToken");
        String userAgent = request.getHeader("user-agent");
        String debugMode = request.getHeader("debug");
        if(!(userAgent.toLowerCase().contains(CommonConstant.ShortcutUA.toLowerCase())
            && userAgent.toLowerCase().contains(CommonConstant.CFNetworkUA.toLowerCase()))){
            if(Objects.isNull(debugMode))
                throw new DefineException(RespCode.ABUSE_TIP);
        }

        response.setContentType("application/json;charset=UTF-8");
        // 如果 userToken 为空，则返回错误响应
        if (StrUtil.isNotBlank(userToken)) {
            if(userService.checkToken(userToken)){
                return true;
            }
            response.getWriter().write(mapper.writeValueAsString(ResultVO.buildDefineError(new DefineException(RespCode.TOKEN_PERMISSION_EXPIRED))));
            return false;
        }
        response.getWriter().write(mapper.writeValueAsString(ResultVO.buildDefineError(new DefineException(RespCode.NO_TOKEN))));
        return false;
    }
}

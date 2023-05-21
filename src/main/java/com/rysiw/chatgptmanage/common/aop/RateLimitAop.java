package com.rysiw.chatgptmanage.common.aop;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.rysiw.chatgptmanage.common.annotation.RateLimit;
import com.rysiw.chatgptmanage.common.enums.CommonConstant;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import com.rysiw.chatgptmanage.common.vo.ResultVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class RateLimitAop {

    @Resource
    private HttpServletRequest request;

    /**
     * 不同的接口，不同的流量控制
     * map的key为 Limiter.key
     */
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Around("@annotation(com.rysiw.chatgptmanage.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿limit的注解
        RateLimit limit = method.getAnnotation(RateLimit.class);
        if (limit != null) {
            //获取用户token
            String token = request.getHeader("userToken");
            //key作用：不同的接口，不同的流量控制
            String key = token + "_" + limit.key(); //以token和限流的key作为组合key
            RateLimiter rateLimiter = null;
            //验证缓存是否有命中key
            if (!limitMap.containsKey(key)) {
                // 创建令牌桶
                rateLimiter = RateLimiter.create(limit.permitsPerSecond());
                limitMap.put(key, rateLimiter);
                log.info("新建了令牌桶={}，容量={}", key, limit.permitsPerSecond());
            }
            rateLimiter = limitMap.get(key);
            // 拿令牌
            boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
            // 拿不到命令，直接返回异常提示
            if (!acquire) {
                log.error("令牌桶={}，获取令牌失败",key);
                throw new DefineException(RespCode.ERROR.getCode(), limit.msg());
            }
        }
        return joinPoint.proceed();
    }
}


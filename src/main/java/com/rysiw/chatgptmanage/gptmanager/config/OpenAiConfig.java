package com.rysiw.chatgptmanage.gptmanager.config;

import cn.hutool.core.collection.ListUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.rysiw.chatgptmanage.common.enums.CommonConstant;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    /**
     * OpenAi的key
     */
    private String keys;

    /**
     * OpenAi的model
     */
    private String model;

    /**
     * OpenAi的max_tokens
     */
    private Integer maxTokens;

    /**
     * OpenAi的temperature
     */
    private Double temperature;

    /**
     * 综合问答API
     */
    private String openaiApi;

    /**
     * 图片问答API
     */
    private String imageApi;

    /**
     * OpenAi的creditApi
     */
    private String creditApi;

    /**
     * 每个apiKey的限流器
     */
    private Map<String, RateLimiter> apiKeyRateLimiterMap = new ConcurrentHashMap<>();


    /**
     * 随机获取一个ApiKey
     * @return ApiKey
     */
    public String getApiKey() {
        // 转为 List
        List<String> keyList = ListUtil.toList(keys.split(","));
        if(keyList.size() == 1){
            return keyList.get(0);
        }

        // 打乱顺序
        Collections.shuffle(keyList);
        // 获取1分钟内未超过最大请求次数的 key
        for (String key : keyList) {
            RateLimiter apiKeyRateLimiter = apiKeyRateLimiterMap.get(key);
            if(apiKeyRateLimiter == null){
                apiKeyRateLimiter = RateLimiter.create(1);
                apiKeyRateLimiterMap.put(key, apiKeyRateLimiter);
            }
            if (apiKeyRateLimiterMap.get(key).tryAcquire()) {
                return key;
            }
        }
        // 如果所有 key 都超过了最大请求次数，则等待获取令牌
        for (String key : keyList) {
            if (apiKeyRateLimiterMap.get(key).tryAcquire(3, TimeUnit.SECONDS)) {
                return key;
            }
        }
        log.error("key爆了");
        throw new DefineException(RespCode.GPT_POOL_LIMIT);
    }
}
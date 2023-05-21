package com.rysiw.chatgptmanage.common.utils;

import com.rysiw.chatgptmanage.common.enums.CommonConstant;
import jakarta.annotation.Resource;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class UserAccessStatistics {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 增加用户访问量
     * @param userToken 用户token
     */
    public void incrUserAccessCount(String userToken) {
        String key = CommonConstant.USER_COUNT_PREFIX + userToken;
        RAtomicLong counter = redissonClient.getAtomicLong(key);
        counter.incrementAndGet();
    }

    /**
     * 获取指定用户的访问量
     * @param userToken 用户token
     * @return 用户访问量
     */
    public Long getUserAccessCount(String userToken) {
        String key = CommonConstant.USER_COUNT_PREFIX + userToken;
        RAtomicLong counter = redissonClient.getAtomicLong(key);
        return counter.get();
    }

    /**
     * 获取所有用户的访问量
     * @return 所有用户的访问量之和
     */
    public Long getTotalAccessCount() {
        long totalAccessCount = 0L;
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("user_access_count:*");
        for (String key : keys) {
            RAtomicLong counter = redissonClient.getAtomicLong(key);
            totalAccessCount += counter.get();
        }
        return totalAccessCount;
    }
}

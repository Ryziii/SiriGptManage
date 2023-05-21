package com.rysiw.chatgptmanage.common.enums;

public interface CommonConstant {

    Integer ChatContextLength = 4;

    String USER_COUNT_PREFIX = "user_access_count:";

    /**
     * 限制每个ApiKey在1分钟内的请求次数
     */
    int GPT_MAX_REQUESTS_PER_MINUTE = 5;

    String ShortcutUA = "Shortcut";

    String CFNetworkUA = "CFNetwork";
}

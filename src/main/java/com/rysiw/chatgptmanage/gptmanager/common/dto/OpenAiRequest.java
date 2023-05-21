package com.rysiw.chatgptmanage.gptmanager.common.dto;

import lombok.Data;

@Data
public class OpenAiRequest {

    /**
     * 请求类型：1文本，2图片，3余额
     */
    private Integer type;

    /**
     * 连续对话
     */
    private String keep;

    /**
     * 问题
     */
    private String content;

    /**
     * 连续对话的问题
     */
    private String keepText;

    /**
     * apiKey
     */
    private String apikey;

    /**
     * userToken
     */
    private String userToken;
}

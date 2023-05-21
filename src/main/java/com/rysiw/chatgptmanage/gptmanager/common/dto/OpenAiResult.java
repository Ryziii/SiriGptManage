package com.rysiw.chatgptmanage.gptmanager.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OpenAiResult {

    /**
     * 状态码
     */
    private Integer code;

    /** 问题 */
    private String title;

    /** 答案 */
    private String html;

    /** 图片答案 */
    private String url;
}

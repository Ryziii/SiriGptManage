package com.rysiw.chatgptmanage.common.dto;

import com.rysiw.chatgptmanage.gptmanager.common.dto.ChatCompletionRequestDTO;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatisticsInfoDTO {

    private String userToken;
    private String queryCount;
    private ChatCompletionRequestDTO chatCompletionRequestDTO;
    private ChatCompletionResult chatCompletionResult;

}

package com.rysiw.chatgptmanage.gptmanager.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatCompletionRequestDTO {
    String model;
    List<ChatMessageDTO> messages;
    Double temperature;
    Double topP;
    Integer n;
    Boolean stream;
    List<String> stop;
    Integer maxTokens;
    Double presencePenalty;
    Double frequencyPenalty;
    Map<String, Integer> logitBias;
    String user;
}

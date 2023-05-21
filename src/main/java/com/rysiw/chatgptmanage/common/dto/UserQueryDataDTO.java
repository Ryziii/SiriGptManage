package com.rysiw.chatgptmanage.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserQueryDataDTO {

    private String userToken;
    private String question;
    private String answer;
}

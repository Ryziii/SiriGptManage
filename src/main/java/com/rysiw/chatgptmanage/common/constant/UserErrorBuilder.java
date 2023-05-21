package com.rysiw.chatgptmanage.common.constant;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserErrorBuilder {

    private String errorMsg;
    private String apiKey;
    private String userToken;
}

package com.rysiw.chatgptmanage.common.enums;

import lombok.Getter;

@Getter
public enum UserEnums {

    ACCOUNT_ENABLE("ENABLE","1"),
    ACCOUNT_DISABLE("DISENABLE","0"),
    ACCOUNT_LONG_ENABLE("DISENABLE","-1"),
    DEFAULT_VALID_HOURS("DEFAULT_VALID_DAYS","30");


    private String key;
    private String value;

    UserEnums(String key,String value){
        this.key = key;
        this.value = value;
    }
}

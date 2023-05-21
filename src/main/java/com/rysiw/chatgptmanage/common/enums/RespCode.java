package com.rysiw.chatgptmanage.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RespCode {

    SUCCESS("200","success"),
    ERROR("A0000","服务器内部错误"),
    INTERNAL_ERROR("A1000","服务器内部错误，请联系管理员"),
    TOKEN_ALIVE("A0001", "用户token存活，无需重新注册"),
    CANT_FIND_USER("A0002", "未找到用户"),
    PASSWORD_WRONG("A0003", "密码错误"),
    REDIS_ERROR("A0004", "存入redis错误"),
    JWT_PARSE_ERROR("A0004", "JWT解析异常"),
    TOKEN_DONT_MATCH("A0005", "token与用户名不匹配"),
    REQUEST_OPENAI_ERROR("A0006","ChatGPT官方服务器被挤爆啦，喝口水后再来试试看吧~"),
    GPT_POOL_LIMIT("A0007","服务器被挤爆啦，喝口水后再来试试看吧~"),
    ABUSE_TIP("A0008","请勿滥用！"),

    //用户权限相关
    NO_TOKEN("A2001","激活码不存在，请您进入本快捷指令填入激活码开启无需翻墙的顺畅ChatGPT体验吧。\n\n最低仅需一元噢~\n\n如需购买激活码请前往https://afdian.net/a/rysiw/plan，购买地址已复制到您的剪切板。"),
    TOKEN_FAILED("A2003","激活码验证失败，请您进入本快捷指令填入正确激活码开启无需翻墙的ChatGPT体验噢。\n\n最低仅需一元噢~\n\n如需购买激活码请前往https://afdian.net/a/rysiw/plan，购买地址已复制到您的剪切板。"),
    TOKEN_PERMISSION_EXPIRED("A2002","激活码已过期，重享无需翻墙的顺畅ChatGPT快去购买吧~最低仅需一元噢~\n\n如需购买激活码请前往https://afdian.net/a/rysiw/plan，购买地址已复制到您的剪切板。");


    private final String code;
    private final String msg;
}

package com.rysiw.chatgptmanage.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yaml")
@Getter
public class BaseConfig {

    @Value("${base.password}")
    private String basePassword;
}

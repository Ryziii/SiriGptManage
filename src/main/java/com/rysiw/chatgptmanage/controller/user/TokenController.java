package com.rysiw.chatgptmanage.controller.user;


import com.rysiw.chatgptmanage.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/token")
public class TokenController {

    @Resource
    private UserService userService;

    @GetMapping("/check/{token}")
    public Boolean checkToken(@PathVariable(value = "token") String token){
        return userService.checkToken(token);
    }

    @GetMapping("/generateKey/{password}/{validDays}")
    public String generateKey(@PathVariable(value = "password") String password,
                              @PathVariable(required = false,value = "validDays") String validDays){
        return userService.generateKey(password, validDays);
    }

    @GetMapping("/generateTenKey/{password}/{num}/{validPeriod}")
    public String generateTenKey(@PathVariable(value = "password") String password,
                              @PathVariable(value = "num") Integer num,
                              @PathVariable(required = false,value = "validPeriod") String validPeriod){
        return userService.generateTenKey(password, num, validPeriod);
    }


    @GetMapping("/updateKey/{password}/{token}/{validDays}")
    public Boolean updateKey(@PathVariable(value = "password") String password,
                            @PathVariable(value = "token") String token,
                            @PathVariable(required = false, value = "validDays") String validDays){
        return userService.updateKey(password, token, validDays);
    }
}

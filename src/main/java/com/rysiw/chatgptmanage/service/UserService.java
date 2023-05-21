package com.rysiw.chatgptmanage.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rysiw.chatgptmanage.common.enums.RespCode;
import com.rysiw.chatgptmanage.common.exception.DefineException;
import com.rysiw.chatgptmanage.config.BaseConfig;
import com.rysiw.chatgptmanage.dao.UserMapper;
import com.rysiw.chatgptmanage.entity.UserEntity;
import com.rysiw.chatgptmanage.common.enums.UserEnums;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    @Resource
    private BaseConfig baseConfig;

    public Boolean checkToken(String token) {
        UserEntity userEntity = this.getOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getToken,token));
        if(Objects.isNull(userEntity))
            throw new DefineException(RespCode.TOKEN_FAILED);
        if(Integer.parseInt(UserEnums.ACCOUNT_DISABLE.getValue()) == userEntity.getIsEnabled()){
            userEntity.setIsEnabled(Integer.parseInt(UserEnums.ACCOUNT_ENABLE.getValue()));
            userEntity.setUpdateTime(DateTime.now());
            this.updateById(userEntity);
        }
        boolean isNotExpired = userEntity.getValidPeriod() > DateUtil.between(DateUtil.date(userEntity.getUpdateTime()),DateUtil.date(), DateUnit.MINUTE);
        return UserEnums.ACCOUNT_ENABLE.getValue().equals(String.valueOf(userEntity.getIsEnabled())) && isNotExpired;

    }

    public String generateKey(String password, String validDays) {
        if (StrUtil.isBlank(validDays))
            validDays = UserEnums.DEFAULT_VALID_HOURS.getValue();
        if(StrUtil.isNotBlank(password) && password.equals(baseConfig.getBasePassword())){
            String uuid = UUID.randomUUID().toString().toUpperCase().replace("-","");
            //TODO createUser应该通过解密方式解密password得到
            this.save(UserEntity.builder()
                    .token(uuid)
                    .createUser(password)
                    .updateUser(password)
                    .createTime(DateTime.now())
                    .updateTime(DateTime.now())
                    .isEnabled(Integer.parseInt(UserEnums.ACCOUNT_DISABLE.getValue()))
                    .validPeriod(Long.parseLong(validDays))
                    .build());
            return uuid;
        }
        return "请勿滥用！";
    }

    public Boolean updateKey(String password, String token, String validDays) {
        if (StrUtil.isBlank(validDays))
            validDays = UserEnums.DEFAULT_VALID_HOURS.getValue();
        if(StrUtil.isNotBlank(password) && password.equals(baseConfig.getBasePassword())){
            //TODO createUser应该通过解密方式解密password得到
            return this.update(UserEntity.builder()
                    .token(token)
                    .createUser(password)
                    .updateUser(password)
                    .createTime(DateTime.now())
                    .updateTime(DateTime.now())
                    .isEnabled(Integer.parseInt(UserEnums.ACCOUNT_ENABLE.getValue()))
                    .validPeriod(Long.valueOf((validDays)))
                    .build()
                , new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getToken,token));
        }
        return false;
    }

    public String generateTenKey(String password, Integer num, String validPeriod) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < num; i++ ) {
            sb.append(generateKey(password, validPeriod));
            sb.append("\n");
        }
        return sb.toString();
    }
}

package com.rysiw.chatgptmanage.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rysiw.chatgptmanage.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}

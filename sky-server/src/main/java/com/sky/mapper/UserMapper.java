package com.sky.mapper;

import com.sky.entity.User;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openId}")
    User getByOpenId(String openId);

    /**
     * 插入新用户
     * @param user
     */
    void insert(User user);

    Integer countByMap(Map map);
}

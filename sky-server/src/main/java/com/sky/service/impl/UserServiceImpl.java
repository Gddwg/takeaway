package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.sky.constant.MessageConstant;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private static final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openId = getOpenId(userLoginDTO.getCode());
        if(openId == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.getByOpenId(openId);
        if(user == null){
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }
    public String getOpenId(String code){
        String appid = weChatProperties.getAppid();
        String secret = weChatProperties.getSecret();
        String url = MessageFormat.format(WECHAT_LOGIN_URL, appid, secret, code);
        log.info("url:{}", url);
        String result = restTemplate.getForObject(url, String.class);
        Map<String, String> parse = (Map<String, String>) JSONObject.parse(result);
        String openid = parse.get("openid");
        return openid;
    }
}

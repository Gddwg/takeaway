package com.sky.config;

import com.sky.utils.RedisUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitConfig implements ApplicationRunner {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public void run(ApplicationArguments args) {
        new RedisUtil(stringRedisTemplate);
    }
}

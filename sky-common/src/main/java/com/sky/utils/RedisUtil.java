package com.sky.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.sky.constant.RedisConstants.CACHE_NULL_TTL;


public class RedisUtil {
    private static StringRedisTemplate stringRedisTemplate;

    public static StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public static String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public static void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, unit);
    }

    public static void set(String key, Object value){
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    public static <R ,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbfallback, Long time, TimeUnit unit){
        String key = keyPrefix + id;
        String json = get(key);
        if(StringUtils.isNotBlank(json)){
            return JSON.toJavaObject(JSON.parseObject(json), type);
        }
        if(json != null){
            return null;
        }
        R r = dbfallback.apply(id);
        if(r == null){
            set(key, " ", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        set(key,r,time,unit);
        return r;
    }

    public static <R ,ID> List<R> queryListWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, List<R>> dbfallback, Long time, TimeUnit unit){
        String key = keyPrefix + id;
        String json = get(key);
        if(StringUtils.isNotBlank(json)){
            return JSONArray.parseArray(json,type);
        }
        if(json != null){
            return null;
        }
        List<R> r = dbfallback.apply(id);
        if(r == null){
            set(key, " ", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        set(key,r,time,unit);
        return r;
    }
}

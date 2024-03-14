package com.sky.redis;

import com.alibaba.fastjson.JSONArray;
import com.sky.constant.RedisConstants;
import com.sky.entity.Dish;
import com.sky.utils.RedisUtil;
import com.sky.vo.DishVO;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DishRedis {

    public static void set(){

    }

    public static List<DishVO> get(Long categoryId){
        String key = RedisConstants.DISH_LIST + categoryId;
        String json = RedisUtil.get(key);
        return JSONArray.parseArray(json,DishVO.class);
    }

    public static List<DishVO> queryList(Long categoryId,Function<Long,List<DishVO>> function){
        return RedisUtil.queryListWithPassThrough(RedisConstants.DISH_LIST,categoryId,DishVO.class,function,RedisConstants.DISH_LIST_TTL, TimeUnit.DAYS);
    }

    public static void remove(Long categoryId){
        String key = RedisConstants.DISH_LIST + categoryId;
        RedisUtil.getStringRedisTemplate().delete(key);
    }
}

package com.sky.context;

import com.sky.constant.JwtClaimsConstant;

import java.util.HashMap;
import java.util.Map;

public class BaseContext {

    public static ThreadLocal<Map> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        threadLocal.get().put(key,value);
    }

    public static Object get(String key) {
        return threadLocal.get().get(key);
    }

    public static void remove() {
        threadLocal.remove();
    }
    public static Long getCurrentId(){
        return (Long)get(JwtClaimsConstant.EMP_ID);
    }

    public static Long getUserId(){
        return (Long)get(JwtClaimsConstant.USER_ID);
    }

}

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
    public static Object getCurrentId(){
        return get(JwtClaimsConstant.EMP_ID);
    }

}

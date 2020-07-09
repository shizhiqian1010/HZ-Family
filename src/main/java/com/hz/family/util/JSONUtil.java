package com.hz.family.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class JSONUtil {

    public static <T> T getObject(String jsonStr, Class<T> type) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        T t = JSON.toJavaObject(jsonObject, type);
        return t;
    }

    public static String toJSON(Object o) {
        return JSON.toJSONString(o);
    }
}

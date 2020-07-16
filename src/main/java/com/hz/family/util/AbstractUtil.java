package com.hz.family.util;

import java.util.UUID;

public abstract class AbstractUtil {
    /**
     * 生成用户 ID
     * @return 用户ID 字符串
     */
    public static String UUID(){
        return UUID.randomUUID().toString();
    }
}

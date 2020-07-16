package com.hz.family.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.UUID;

/**
 * @author ZhiQian
 *
 * 用户工具类
 *
 */
public class UserUtil extends AbstractUtil{

    // Redis 缓存的 用户信息 key
    public final static String USER_CACHE_KEYS = "User-Table";

    /**
     * 获取当前登录的用户名
     * @return  当前登录的用户名
     */
    public static String getCurrentUserName() {

        Subject subject = SecurityUtils.getSubject();
        String currentUserName = null;
        if (subject != null) {
            currentUserName = (String) subject.getPrincipal();
        }
        return currentUserName;
    }


}

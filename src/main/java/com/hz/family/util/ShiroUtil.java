package com.hz.family.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.UUID;

public class ShiroUtil {
    public static String getCurrentUserName() {

        Subject subject = SecurityUtils.getSubject();
        String currentUserName = null;
        if (subject != null) {
            currentUserName = (String) subject.getPrincipal();
        }
        return currentUserName;
    }

    public static String UUID(){
        return UUID.randomUUID().toString();
    }
}

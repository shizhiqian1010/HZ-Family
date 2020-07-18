package com.hz.family.entity;

import sun.rmi.runtime.Log;

import java.io.Serializable;
import java.util.Date;

public class Blog implements Serializable {

    private final static Long serialVersionUID  = 5844966552335546L;

    private String id;

    private String userName;
    /**
     * 日记详情
     */
    private String logInfo;
    /**
     * 发布时间
     */
    private Date releaseTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

}

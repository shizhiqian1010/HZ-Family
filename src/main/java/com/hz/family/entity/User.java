package com.hz.family.entity;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {

    private final static Long serialVersionUID  = 5844966555589435546L;

    private String Id;

    private String userName;

    private String passWord;

    private Set<String> roles;

//    private Set<String> permissions;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public User() {
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String id, String userName, String passWord, Set<String> roles) {
        Id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.roles = roles;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

//    public Set<String> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<String> permissions) {
//        this.permissions = permissions;
//    }
}

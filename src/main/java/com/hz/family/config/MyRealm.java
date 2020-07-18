package com.hz.family.config;

import com.hz.family.entity.User;
import com.hz.family.util.UserUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class MyRealm extends AuthorizingRealm {
    @Autowired
    RedisTemplate redisTemplate;

//    public MyRealm(){
//        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//        hashedCredentialsMatcher.setHashAlgorithmName("md5");
//        hashedCredentialsMatcher.setHashIterations(1);
//        this.setCredentialsMatcher(hashedCredentialsMatcher);
//    }

    //授权登录用户
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("principals should not be null");
        }
        User user = (User) principals.getPrimaryPrincipal();
        User object = (User) redisTemplate.opsForHash().get(UserUtil.USER_CACHE_KEYS, user.getUserName());

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(object.getRoles());

        return simpleAuthorizationInfo;
    }


    // 用户登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        User user = (User) redisTemplate.opsForHash().get(UserUtil.USER_CACHE_KEYS, usernamePasswordToken.getUsername());
        if (user == null) {
            throw new UnknownAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                usernamePasswordToken.getUsername(),
                user.getPassWord(),
                null,
                getName()
        );
//        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("123456"));
        return authenticationInfo;
    }

    /**
     * 初始化一个 admin 用户
     */
    @PostConstruct
    private void initUser() {
        User user = new User();
        user.setId(UserUtil.UUID());
        user.setUserName("admin");
        user.setPassWord("123456");
        Set<String> role = new HashSet<>();
        role.add("管理员");
        role.add("普通成员");
        user.setRoles(role);
        redisTemplate.opsForHash().put(UserUtil.USER_CACHE_KEYS, user.getUserName(), user);
    }
}

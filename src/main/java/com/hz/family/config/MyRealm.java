package com.hz.family.config;

import com.hz.family.entity.User;
import com.hz.family.util.JSONUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyRealm extends AuthorizingRealm {
    @Autowired
    RedisTemplate redisTemplate;

    private String permission = "User_Role_Permission_";
    private String role = "User_Role_";

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


        Object userJson = redisTemplate.opsForValue().get(user.getUserName());
        User object = JSONUtil.getObject(userJson.toString(), User.class);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(object.getRoles());

        return simpleAuthorizationInfo;
    }

    // 用户登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        Object userJson = redisTemplate.opsForValue().get(usernamePasswordToken.getUsername());

        if (userJson == null) {
            throw new UnknownAccountException();
        }
        User user = JSONUtil.getObject(userJson.toString(), User.class);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                usernamePasswordToken.getUsername(),
                user.getPassWord(),
                null,
                getName()
        );
//        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("123456"));
        return authenticationInfo;
    }
}

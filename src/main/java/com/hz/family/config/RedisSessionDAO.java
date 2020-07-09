package com.hz.family.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisSessionDAO extends AbstractSessionDAO {

    private final static Logger log = LoggerFactory.getLogger(RedisSessionDAO.class);


    RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final static String SHIRO_SESSION_PREFIX = "shiro-session:";


    @Override
    protected Serializable doCreate(Session session) {
        Serializable serializable = generateSessionId(session);
        assignSessionId(session, serializable);
        redisTemplate.opsForValue().set(SHIRO_SESSION_PREFIX + session.getId(), session);
        setExpire(SHIRO_SESSION_PREFIX + session.getId(), null);
        return serializable;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        SimpleSession json = (SimpleSession) redisTemplate.opsForValue().get(SHIRO_SESSION_PREFIX + sessionId);
        Session session = null;
        if (json != null) {
            session = json;
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        SimpleSession json = (SimpleSession) redisTemplate.opsForValue().get(SHIRO_SESSION_PREFIX + session.getId());
        if (json != null) {
            redisTemplate.opsForValue().set(SHIRO_SESSION_PREFIX + session.getId(), session);
            setExpire(SHIRO_SESSION_PREFIX + session.getId(), null);
        } else {
            throw new UnknownSessionException("not find sessionId : " + session.getId().toString() + " ");
        }
    }

    @Override
    public void delete(Session session) {
        redisTemplate.delete(SHIRO_SESSION_PREFIX + session.getId());
        log.info("delete session success;sessionId: {}", session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<String> keys = redisTemplate.keys(SHIRO_SESSION_PREFIX);
        List<Session> list = new LinkedList<>();
        if (CollectionUtils.isEmpty(keys)) {
            return list;
        }
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            SimpleSession json = (SimpleSession) redisTemplate.opsForValue().get(next);
            list.add(json);
        }
        return list;
    }

    private void setExpire(String keyName, Integer timeOut) {
        if (timeOut == null){
            timeOut = 60 * 30;
        }
        redisTemplate.expire(keyName, timeOut, TimeUnit.SECONDS);
    }

}

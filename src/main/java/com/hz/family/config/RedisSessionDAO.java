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

/**
 * Session 缓存类操作类
 * 维护一个全局的 Hash 表和每个 Session 独有的 String 类型的 key - value
 * 通过 String 类型的键值维护来卡控 Session 的过期时间
 * Hash 表格式： key: shiro-session-hash ; HashKey：shiro-session: + sessionId ; HashValue: session
 * String 类型键： key: shiro-session: + sessionId ; value: sessionId
 */
public class RedisSessionDAO extends AbstractSessionDAO {

    private final static Logger log = LoggerFactory.getLogger(RedisSessionDAO.class);


    RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final static String SHIRO_SESSION_PREFIX = "shiro-session:";

    private final static String SHIRO_SESSION_HASH_PREFIX = "shiro-session-hash";

    @Override
    protected Serializable doCreate(Session session) {
        Serializable serializable = generateSessionId(session);
        assignSessionId(session, serializable);
        setSessionForStr(session.getId());
        putSessionHash(session.getId(), session);
        return serializable;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Object keyId = getSessionForStr(sessionId);
        Session session = null;
        if (keyId != null) {
            session = (SimpleSession) getSessionHash(keyId.toString());
        } else {
            deleteSessionHash(sessionId);
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        Object sessionId = getSessionForStr(session.getId());
        if (sessionId != null) {
            putSessionHash((Serializable) sessionId, session);
        } else {
            deleteSessionHash((Serializable) sessionId);
        }
    }

    @Override
    public void delete(Session session) {
        deleteSessionForStr(session.getId());
        deleteSessionHash(session.getId());
        log.info("delete session success;sessionId: {}", session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set keysId = redisTemplate.opsForHash().keys(SHIRO_SESSION_HASH_PREFIX);
        List sessionList = redisTemplate.opsForHash().values(SHIRO_SESSION_HASH_PREFIX);

        List currentSessionId = redisTemplate.opsForValue().multiGet(keysId);

        List<Session> list = new LinkedList<>();
        if (!CollectionUtils.isEmpty(sessionList) && !CollectionUtils.isEmpty(currentSessionId)) {
            Iterator iterator = sessionList.iterator();
            while (iterator.hasNext()) {
                SimpleSession next = (SimpleSession) iterator.next();
                if (currentSessionId.contains(next.getId())) {
                    list.add(next);
                } else {
                    deleteSessionHash(next.getId());
                }
            }
        }
        return list;

    }

    private void setExpire(String keyName, Integer timeOut) {
        if (timeOut == null) {
            timeOut = 60 * 30;
        }
        redisTemplate.expire(keyName, timeOut, TimeUnit.SECONDS);
    }

    private void setSessionForStr(Serializable sessionId) {
        redisTemplate.opsForValue().set(SHIRO_SESSION_PREFIX + sessionId, sessionId);
        setExpire(SHIRO_SESSION_PREFIX + sessionId, null);
    }

    private Object getSessionForStr(Serializable sessionId) {
        Object sessionkey = redisTemplate.opsForValue().get(SHIRO_SESSION_PREFIX + sessionId);
        return sessionkey == null ? null : sessionkey;
    }

    private void deleteSessionForStr(Serializable sessionId) {
        redisTemplate.delete(SHIRO_SESSION_PREFIX + sessionId);
    }

    private Object getSessionHash(Serializable sessionId) {
        Object session = redisTemplate.opsForHash().get(SHIRO_SESSION_HASH_PREFIX, SHIRO_SESSION_PREFIX + sessionId);
        setExpire(SHIRO_SESSION_PREFIX + sessionId, null);
        return session == null ? null : session;
    }

    private void putSessionHash(Serializable sessionId, Session session) {
        redisTemplate.opsForHash().put(SHIRO_SESSION_HASH_PREFIX, SHIRO_SESSION_PREFIX + sessionId, session);
        setExpire(SHIRO_SESSION_PREFIX + sessionId, null);
    }

    private void deleteSessionHash(Serializable sessionId) {
        redisTemplate.opsForHash().delete(SHIRO_SESSION_HASH_PREFIX, SHIRO_SESSION_PREFIX + sessionId);
    }
}

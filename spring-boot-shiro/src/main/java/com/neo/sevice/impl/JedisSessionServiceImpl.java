package com.neo.sevice.impl;

import com.neo.sevice.JedisSessionService;
import com.neo.util.RedisUtil;
import com.neo.util.SerializeUtil;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Session 管理
 * @author sojson.com
 *
 */
@SuppressWarnings("unchecked")
@Service
public class JedisSessionServiceImpl implements JedisSessionService {
    public static final String REDIS_SHIRO_SESSION = "sojson-shiro-demo-session:";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void saveSession(Session session) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            byte[] value = SerializeUtil.serialize(session);
            /*
            直接使用 (int) (session.getTimeout() / 1000) 的话，session失效和redis的TTL 同时生效
             */
            redisUtil.set(buildRedisSessionKey(session.getId()), value, (Long) (session.getTimeout() / 1000));
        } catch (Exception e) {
            logger.error("save session error，id:[%s]",session.getId());
        }
    }

    @Override
    public void deleteSession(Serializable id) {
        if (id == null) {
            throw new NullPointerException("session id is empty");
        }
        try {
            redisUtil.remove(buildRedisSessionKey(id));
        } catch (Exception e) {
            logger.error("删除session出现异常，id:[%s]",id);
        }
    }

   
	@Override
    public Session getSession(Serializable id) {
        if (id == null)
        	 throw new NullPointerException("session id is empty");
        Session session = null;
        try {
            byte[] value = (byte[]) redisUtil.get(buildRedisSessionKey(id));
            session = SerializeUtil.deserialize(value, Session.class);
        } catch (Exception e) {
            logger.error("获取session异常，id:[%s]",id);
        }
        return session;
    }

//    @Override
//    public Collection<Session> getAllSessions() {
//    	Collection<Session> sessions = null;
//		try {
//			sessions = redisUtil.getJedisManager().AllSession(DB_INDEX,REDIS_SHIRO_SESSION);
//		} catch (Exception e) {
//			LoggerUtils.fmtError(getClass(), e, "获取全部session异常");
//		}
//
//        return sessions;
//    }

    private String buildRedisSessionKey(Serializable sessionId) {
        return REDIS_SHIRO_SESSION + sessionId;
    }

}

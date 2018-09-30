package com.neo.shiro;

import com.neo.sevice.JedisSessionService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Session 操作
 */
@Component
public class CustomSessionManager extends AbstractSessionDAO {


    @Autowired
    private JedisSessionService jedisShiroSession;

    @Override
    public void update(Session session) throws UnknownSessionException {
        jedisShiroSession.saveSession(session);
    }  
  
    @Override  
    public void delete(Session session) {
        if (session == null) {  
            return;
        }
        Serializable id = session.getId();  
        if (id != null)
            jedisShiroSession.deleteSession(id);
    }
  
    @Override
    public Collection<Session> getActiveSessions() {
        return null;
//        return getJedisShiroSession().getAllSessions();
    }
  
    @Override  
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);  
        this.assignSessionId(session, sessionId);
        jedisShiroSession.saveSession(session);
        return sessionId;  
    }
  
    @Override  
    protected Session doReadSession(Serializable sessionId) {
        return jedisShiroSession.getSession(sessionId);
    }
}

package com.github.thomasandre84.sftp;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MinaSshSessionFactory implements KeyedPooledObjectFactory<String, MinaSshSession> {

    private static final Logger log = LoggerFactory.getLogger(MinaSshSessionFactory.class);

    public MinaSshSessionFactory() {
    }

    @Override
    public void activateObject(String s, PooledObject<MinaSshSession> pooledObject) {
        log.info("Activating MinaSshSession for {} and client {}", s, pooledObject.getObject().getSshClient());
    }

    @Override
    public void destroyObject(String s, PooledObject<MinaSshSession> pooledObject) throws Exception {
        log.info("Destroying MinaSshSession for {} - client {}", s, pooledObject.getObject().getSshClient());
        pooledObject.getObject().close();
    }

    @Override
    public PooledObject<MinaSshSession> makeObject(String s) throws Exception {
        log.info("Creating MinaSshSession for {}", s);
        MinaSshSession session = new MinaSshSession(s);
        session.connect();
        return new DefaultPooledObject<>(session);
    }

    @Override
    public void passivateObject(String s, PooledObject<MinaSshSession> pooledObject) {
        log.info("Passivating MinaSshSession for {} and client {}", s, pooledObject.getObject().getSshClient());
    }

    @Override
    public boolean validateObject(String s, PooledObject<MinaSshSession> pooledObject) {
        log.info("Validating MinaSshSession for {} and client {}", s, pooledObject.getObject().getSshClient());
        ClientSession session = pooledObject.getObject().getSession();
        return session.isOpen() && session.getSessionState().contains(ClientSession.ClientSessionEvent.AUTHED);
    }

}

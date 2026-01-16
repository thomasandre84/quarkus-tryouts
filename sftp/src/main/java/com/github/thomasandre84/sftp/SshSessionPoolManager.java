package com.github.thomasandre84.sftp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

@ApplicationScoped
public class SshSessionPoolManager {

    private GenericKeyedObjectPool<String, MinaSshSession> keyedObjectPool;

    private final MinaSshSessionFactory factory;
    private final GenericKeyedObjectPoolConfig<MinaSshSession> poolConfig;

    @Inject
    public SshSessionPoolManager(MinaSshSessionFactory factory,
                         @Named("sshSessionPoolConfig") GenericKeyedObjectPoolConfig<MinaSshSession> poolConfig) {
        this.factory = factory;
        this.poolConfig = poolConfig;
    }

    @PostConstruct
    void init() {
        keyedObjectPool = new GenericKeyedObjectPool<>(factory, poolConfig);
    }

    public MinaSshSession borrowClient(final String host) throws Exception {
        return keyedObjectPool.borrowObject(host);
    }

    public void returnClient(final String host, final MinaSshSession session) {
        keyedObjectPool.returnObject(host, session);
    }

    @PreDestroy
    public void closeAll() {
        keyedObjectPool.close();
    }


}

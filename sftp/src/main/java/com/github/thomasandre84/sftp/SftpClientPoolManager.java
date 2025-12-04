package com.github.thomasandre84.sftp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

@ApplicationScoped
public class SftpClientPoolManager {

    private GenericKeyedObjectPool<String, MinaSftpClient> keyedObjectPool;

    private final MinaSftpClientFactory factory;
    private final GenericKeyedObjectPoolConfig<MinaSftpClient> poolConfig;

    @Inject
    public SftpClientPoolManager(MinaSftpClientFactory factory, GenericKeyedObjectPoolConfig<MinaSftpClient> poolConfig) {
        this.factory = factory;
        this.poolConfig = poolConfig;
    }

    @PostConstruct
    void init() {
        keyedObjectPool = new GenericKeyedObjectPool<>(factory, poolConfig);
    }

    public MinaSftpClient borrowClient(final String host) throws Exception {
        return keyedObjectPool.borrowObject(host);
    }

    public void returnClient(final String host, final MinaSftpClient client) {
        keyedObjectPool.returnObject(host, client);
    }

    @PreDestroy
    public void closeAll() {
        keyedObjectPool.close();
    }


}

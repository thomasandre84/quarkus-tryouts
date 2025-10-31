package com.github.thomasandre84.sftp;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SftpClientPoolManager {
    private final ConcurrentHashMap<String, GenericObjectPool<MinaSftpClient>> pools = new ConcurrentHashMap<>();

    public MinaSftpClient borrowClient(final String host) throws Exception {
        GenericObjectPool<MinaSftpClient> pool = pools.computeIfAbsent(host, k -> {
           MinaSftpClientFactory factory = new MinaSftpClientFactory(host);
           GenericObjectPool objectPool = new GenericObjectPool<>(factory);
           objectPool.setMaxTotal(10);
           objectPool.setMinIdle(2);
           objectPool.setMaxIdle(5);
           objectPool.setEvictorShutdownTimeout(Duration.ofMillis(1000L));
           return objectPool;
        });
        return pool.borrowObject();
    }

    public void returnClient(final String host, final MinaSftpClient client) {
        GenericObjectPool<MinaSftpClient> pool = pools.get(host);
        if (pool != null) {
            pool.returnObject(client);
        }
    }

    @PreDestroy
    public void closeAll() {
        pools.values().forEach(GenericObjectPool::close);
    }


}

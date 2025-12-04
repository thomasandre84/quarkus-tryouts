package com.github.thomasandre84.sftp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.time.Duration;

@ApplicationScoped
public class SftpPoolProducer {

    @Produces
    @ApplicationScoped
    public GenericKeyedObjectPoolConfig<MinaSftpClient> createPoolConfig() {
        GenericKeyedObjectPoolConfig<MinaSftpClient> poolConfig = new GenericKeyedObjectPoolConfig<>();
        poolConfig.setMaxTotalPerKey(5);
        poolConfig.setMinIdlePerKey(1);
        poolConfig.setMaxIdlePerKey(3);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofMinutes(1));
        poolConfig.setMinEvictableIdleTime(Duration.ofMinutes(2));
        return poolConfig;
    }

}

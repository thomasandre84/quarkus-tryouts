package com.github.thomasandre84.sftp;

import com.github.thomasandre84.sftp.client.MinaSftpClient;
import com.github.thomasandre84.sftp.session.MinaSshSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.time.Duration;

@ApplicationScoped
public class PoolConfigProducer {

    @Produces
    @ApplicationScoped
    @Named("sftpClientPoolConfig")
    public GenericKeyedObjectPoolConfig<MinaSftpClient> createSftpClientPoolConfig() {
        GenericKeyedObjectPoolConfig<MinaSftpClient> poolConfig = new GenericKeyedObjectPoolConfig<>();
        poolConfig.setMaxTotalPerKey(5);
        poolConfig.setMinIdlePerKey(1);
        poolConfig.setMaxIdlePerKey(3);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofMinutes(1));
        return poolConfig;
    }

    @Produces
    @ApplicationScoped
    @Named("sshSessionPoolConfig")
    public GenericKeyedObjectPoolConfig<MinaSshSession> createSshSessionPoolConfig() {
        GenericKeyedObjectPoolConfig<MinaSshSession> poolConfig = new GenericKeyedObjectPoolConfig<>();
        poolConfig.setMaxTotalPerKey(1);
        poolConfig.setMinIdlePerKey(0);
        poolConfig.setMaxIdlePerKey(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(20)); // Evict idle sessions after 20 seconds
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(10)); // Run eviction every 10 seconds
        return poolConfig;
    }

}

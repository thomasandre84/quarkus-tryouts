package com.github.thomasandre84.sftp.client;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MinaSftpClientFactory implements KeyedPooledObjectFactory<String, MinaSftpClient> {

    private static final Logger log = LoggerFactory.getLogger(MinaSftpClientFactory.class);

    public MinaSftpClientFactory() {
    }

    @Override
    public void activateObject(String s, PooledObject<MinaSftpClient> pooledObject) {
        log.info("Activating MinaSftpClient for {} and client {}", s, pooledObject.getObject().getSshClient());
    }

    @Override
    public void destroyObject(String s, PooledObject<MinaSftpClient> pooledObject) throws Exception {
        log.info("Destroying MinaSftpClient for {} - client {}", s, pooledObject.getObject().getSshClient());
        pooledObject.getObject().close();
    }

    @Override
    public PooledObject<MinaSftpClient> makeObject(String s) throws Exception {
        log.info("Creating MinaSftpClient for {}", s);
        MinaSftpClient client = new MinaSftpClient(s);
        client.connect();
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void passivateObject(String s, PooledObject<MinaSftpClient> pooledObject) {
        log.info("Passivating MinaSftpClient for {} and client {}", s, pooledObject.getObject().getSshClient());
    }

    @Override
    public boolean validateObject(String s, PooledObject<MinaSftpClient> pooledObject) {
        log.info("Validating MinaSftpClient for {} and client {}", s, pooledObject.getObject().getSshClient());
        return pooledObject.getObject().getSshClient().isStarted();
    }

}

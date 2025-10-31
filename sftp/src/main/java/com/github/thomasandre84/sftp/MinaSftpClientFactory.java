package com.github.thomasandre84.sftp;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MinaSftpClientFactory extends BasePooledObjectFactory<MinaSftpClient> {
    private final String targetHost;

    public MinaSftpClientFactory(String targetHost) {
        this.targetHost = targetHost;
    }

    @Override
    public MinaSftpClient create() throws Exception {
        MinaSftpClient client = new MinaSftpClient(targetHost);
        client.connect();
        return client;
    }

    @Override
    public PooledObject<MinaSftpClient> wrap(MinaSftpClient minaSftpClient) {
        return new DefaultPooledObject<>(minaSftpClient);
    }

    @Override
    public void destroyObject(PooledObject<MinaSftpClient> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<MinaSftpClient> p) {
        return p.getObject().getSshClient().isStarted();
    }
}

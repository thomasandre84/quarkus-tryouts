package com.github.thomasandre84.sftp;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MinaSftpClientFactory implements KeyedPooledObjectFactory<String, MinaSftpClient> {

    public MinaSftpClientFactory() {
    }

    @Override
    public void activateObject(String s, PooledObject<MinaSftpClient> pooledObject) {

    }

    @Override
    public void destroyObject(String s, PooledObject<MinaSftpClient> pooledObject) throws Exception {
        pooledObject.getObject().close();
    }

    @Override
    public PooledObject<MinaSftpClient> makeObject(String s) throws Exception {
        MinaSftpClient client = new MinaSftpClient(s);
        client.connect();
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void passivateObject(String s, PooledObject<MinaSftpClient> pooledObject) {

    }

    @Override
    public boolean validateObject(String s, PooledObject<MinaSftpClient> pooledObject) {
        return pooledObject.getObject().getSshClient().isStarted();
    }

}

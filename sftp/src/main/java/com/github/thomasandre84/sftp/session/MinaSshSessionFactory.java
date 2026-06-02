package com.github.thomasandre84.sftp.session;

import com.github.thomasandre84.sftp.lock.LockService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

@ApplicationScoped
public class MinaSshSessionFactory implements KeyedPooledObjectFactory<String, MinaSshSession> {

    private static final Logger log = LoggerFactory.getLogger(MinaSshSessionFactory.class);
    private static final Duration CREATE_LOCK_TTL = Duration.ofSeconds(15);
    private static final long ACQUIRE_RETRY_DELAY_MS = 200L;
    private static final int ACQUIRE_MAX_ATTEMPTS = 25; // ~5 seconds

    private final LockService lockService;

    @Inject
    public MinaSshSessionFactory(LockService lockService) {
        this.lockService = lockService;
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

        // Serialize concurrent session creation per host across JVMs using a short DB lease.
        // This protects only the connect/establishment phase, not the later usage.
        String resource = "session-create:" + s;
        String owner = UUID.randomUUID().toString();
        boolean acquired = false;
        int attempts = 0;
        try {
            while (!acquired && attempts < ACQUIRE_MAX_ATTEMPTS) {
                acquired = lockService.acquireLock(resource, owner, CREATE_LOCK_TTL);
                if (!acquired) {
                    attempts++;
                    try {
                        Thread.sleep(ACQUIRE_RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for session-create lock for " + s, ie);
                    }
                }
            }
            if (!acquired) {
                throw new IllegalStateException("Could not acquire session-create lock for host " + s + " after " + attempts + " attempts");
            }

            MinaSshSession session = new MinaSshSession(s);
            session.connect();
            return new DefaultPooledObject<>(session);
        } finally {
            if (acquired) {
                try {
                    lockService.releaseLock(resource, owner);
                } catch (Exception e) {
                    log.warn("Failed to release session-create lock for {}: {}", s, e.getMessage(), e);
                }
            }
        }
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

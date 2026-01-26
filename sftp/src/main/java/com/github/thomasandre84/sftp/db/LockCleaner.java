package com.github.thomasandre84.sftp.db;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class LockCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockCleaner.class);

    @Inject
    private SftpLockHandler sftpLockHandler;

    /**
     * Cleanup old Locks
     */
    @Transactional
    @Scheduled(every = "120s")
    public void cleanUp() {
        List<SftpLock> locks = sftpLockHandler.getAllLocks();
        LOGGER.info("Found {} locks", locks.size());
        for (SftpLock lock: locks) {
            if (lock.getExpireTime().isBefore(Instant.now().minusSeconds(120))) {
                LOGGER.info("Deleting Lock: {}", lock);
                sftpLockHandler.deleteLock(lock);
            }
        }
    }
}

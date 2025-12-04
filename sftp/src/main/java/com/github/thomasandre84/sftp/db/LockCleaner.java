package com.github.thomasandre84.sftp.db;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class LockCleaner {

    @Inject
    private SftpLockHandler sftpLockHandler;

    /**
     * Cleanup old Locks
     */
    @Transactional
    @Scheduled(every = "10s")
    public void cleanUp() {
        List<SftpLock> locks = sftpLockHandler.getAllLocks();
        locks.forEach(System.out::println);
    }
}

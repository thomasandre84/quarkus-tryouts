package com.github.thomasandre84.sftp.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class SftpLockHandler {
    @Inject
    private SftpLockRepository sftpLockRepository;

    public List<SftpLock> getAllLocks() {
        return sftpLockRepository.findAll().list();
    }

    @Transactional
    public SftpLock addNew(String target) {
        SftpLock lock = new SftpLock(Instant.now(), Instant.now().plusSeconds(60), target, "this");
        sftpLockRepository.persist(lock);
        return lock;
    }
}

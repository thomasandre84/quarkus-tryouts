package com.github.thomasandre84.sftp.db;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SftpLockRepository implements PanacheRepository<SftpLock> {
}

package com.github.thomasandre84.sftp.channel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@ApplicationScoped
public class SftpChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpChannelService.class);
    private static final ExecutorService executorService = newFixedThreadPool(10);

    private final SftpChannelManager channelProvider;
    private final com.github.thomasandre84.sftp.lock.LockService lockService;

    @Inject
    public SftpChannelService(SftpChannelManager channelProvider,
                              com.github.thomasandre84.sftp.lock.LockService lockService) {
        this.channelProvider = channelProvider;
        this.lockService = lockService;
    }

    public List<String> listDirAsync(String host) {
        LOGGER.info("List folder");
        return listFolder(host);
    }

    public void listDirAsync(String host, int amount) {

    }

    private List<String> listFolder(String host) {
        List<String> dirs = new ArrayList<>();
        String lockOwner = java.util.UUID.randomUUID().toString();
        boolean acquired = false;
        try {
            // try to acquire short lease for this host/resource
            acquired = lockService.acquireLock(host, lockOwner, java.time.Duration.ofSeconds(30));
            if (!acquired) {
                LOGGER.warn("Could not acquire lock for host {}, skipping listing", host);
                return dirs;
            }

            try (ManagedSftpClient channel = channelProvider.getSftpClient(host)) {
                // TODO: actual directory listing using channel.getSftpClient()
            }
        } catch (Exception e) {
            LOGGER.error("Error listing directory for host {}: {}", host, e.getMessage(), e);
        } finally {
            if (acquired) {
                try {
                    lockService.releaseLock(host, lockOwner);
                } catch (Exception ex) {
                    LOGGER.warn("Failed to release lock for host {}: {}", host, ex.getMessage(), ex);
                }
            }
        }

        return dirs;
    }
}

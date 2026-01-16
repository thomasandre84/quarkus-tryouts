package com.github.thomasandre84.sftp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@ApplicationScoped
public class SshSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshSessionService.class);

    private final SshSessionPoolManager sshSessionPoolManager;
    private final ExecutorService executorService;

    @Inject
    public SshSessionService(SshSessionPoolManager sshSessionPoolManager) {
        this.sshSessionPoolManager = sshSessionPoolManager;
        this.executorService = newFixedThreadPool(10);
    }

    public void listSftpHomeDir(String host) {
        MinaSshSession session = null;
        try {
            session = sshSessionPoolManager.borrowClient(host);
            try (SftpClient sftpClient = SftpClientFactory.instance().createSftpClient(session.getSession())) {
                var dir =  sftpClient.readDir(".");
                LOGGER.info("Found SFTP home dir {}", dir);
            } finally {
                LOGGER.info("Returning SFTP Client for host: {}", host);
            }
        } catch (Exception e) {
            LOGGER.error("Error while listing SFTP home directory for host: {}", host, e);
        } finally {
            if (session != null) {
                sshSessionPoolManager.returnClient(host, session);
            }
        }
    }

    public void listSftpHomeDirAsync(String host, int amount) throws InterruptedException {
        for (int i = 0; i < amount; i++) {
            executorService.submit(() -> listSftpHomeDir(host));
        }
        Thread.sleep(5000L);
    }
}

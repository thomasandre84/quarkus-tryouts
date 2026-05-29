package com.github.thomasandre84.sftp.session;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;

@ApplicationScoped
public class SshSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SshSessionService.class);

    private final SshSessionPoolManager sshSessionPoolManager;
    private static final ExecutorService executorService = newFixedThreadPool(10);

    @Inject
    public SshSessionService(SshSessionPoolManager sshSessionPoolManager) {
        this.sshSessionPoolManager = sshSessionPoolManager;
    }

    public List<String> listSftpHomeDir(String host) {
        //MinaSshSession session = null;
        List<String> files = new ArrayList<>();
        try (MinaSshSession session = sshSessionPoolManager.borrowClient(host)){
            //session = sshSessionPoolManager.borrowClient(host);
            try (SftpClient sftpClient = SftpClientFactory.instance().createSftpClient(session.getSession())) {
                var dir =  sftpClient.readDir(".");

                dir.iterator().forEachRemaining(f -> files.add(f.getFilename()));
                LOGGER.info("Found SFTP home dir  with files: {}", files);
            } finally {
                LOGGER.info("Returning SFTP Client for host: {}", host);
            }
        } catch (Exception e) {
            LOGGER.error("Error while listing SFTP home directory for host: {}", host, e);
        }

        return files;
    }

    public void listSftpHomeDirAsync(String host, int amount) throws InterruptedException, ExecutionException {
        var futures = new ArrayList<Future<List<String>>>();
        //List<String> files = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            LOGGER.info("Listening folder the {} time", i);
            Future<List<String>> future = executorService.submit(() -> listSftpHomeDir(host));
            futures.add(future);
        }

        for (Future<?> future : futures) {
            future.get();
        }

    }
}

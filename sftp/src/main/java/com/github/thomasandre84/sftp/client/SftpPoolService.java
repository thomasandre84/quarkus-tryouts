package com.github.thomasandre84.sftp.client;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Dependent
public class SftpPoolService {
    static final Logger log = LoggerFactory.getLogger(SftpPoolService.class);

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Inject
    SftpClientPoolManager sftpClientPoolManager;


    public void createLocalPooledConnections(String host, int amount) throws Exception {
        log.info("Creating local connections for host {} and amount {}", host, amount);
        for (int i = 0; i < amount; i++ ) {
            MinaSftpClient client = sftpClientPoolManager.borrowClient(host);
            log.info("Borrowed connection for host {} and number {}", host, i);
            sftpClientPoolManager.returnClient(host, client);
        }

    }

    public void createLocalPooledAsync(String host, int amount) throws InterruptedException {
        log.info("Creating local connections for host {} and amount {} async", host, amount);
        CountDownLatch latch = new CountDownLatch(amount);
        for (int i = 0; i < amount; i++) {
            Runnable runnable = new AsyncRunner(host, i, sftpClientPoolManager, latch);
            executorService.submit(runnable);
        }
        latch.await();
    }

}

class AsyncRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(AsyncRunner.class);
    String host;
    int number;
    SftpClientPoolManager poolManager;
    CountDownLatch latch;

    public AsyncRunner(String host, int number, SftpClientPoolManager poolManager, CountDownLatch latch) {
        this.host = host;
        this.number = number;
        this.poolManager = poolManager;
        this.latch = latch;
    }

    @Override
    public void run() {
        MinaSftpClient client = null;
        try {
            client = poolManager.borrowClient(host);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Borrowed connection for host {} and number {}", host, number);
        poolManager.returnClient(host, client);
        latch.countDown();
    }
}

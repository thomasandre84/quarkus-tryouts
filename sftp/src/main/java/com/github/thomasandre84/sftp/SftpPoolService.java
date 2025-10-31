package com.github.thomasandre84.sftp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SftpPoolService {
    private static final Logger log = LoggerFactory.getLogger(SftpPoolService.class);

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
}

package com.github.thomasandre84.sftp;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MinaSshSession {
    private static final Logger logger = LoggerFactory.getLogger(MinaSshSession.class);

    private static final Path keyPath = Paths.get(System.getProperty("user.home"), ".ssh",  "id_rsa");
    private static final String username = "thommi";
    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    private final String targetHost;
    private SshClient sshClient;

    private ClientSession session;

    public MinaSshSession(String targetHost) {
        this.targetHost = targetHost;
    }

    public void connect() throws IOException {
        sshClient = SshClient.setUpDefaultClient();
        KeyIdentityProvider keyIdentityProvider = new FileKeyPairProvider(keyPath);
        sshClient.setKeyIdentityProvider(keyIdentityProvider);
        sshClient.start();
        session = sshClient.connect(username, targetHost, 22)
                .verify(TIMEOUT)
                .getClientSession();
        session.auth().verify(TIMEOUT);
        session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.SECONDS, 30);

        logger.info("Connected to SFTP server {}",  targetHost);
    }

    public ClientSession getSession() {
        return session;
    }

    public SshClient getSshClient() {
        return sshClient;
    }

    public void close() throws IOException {
        session.close();
        sshClient.close();
    }
}

package com.github.thomasandre84.sftp;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class MinaSftpClient {
    private static final Logger logger = LoggerFactory.getLogger(MinaSftpClient.class);

    private static final Path keyPath = Paths.get(System.getProperty("user.home"), ".ssh",  "id_rsa");
    private static final String username = "thommi";
    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    private final String targetHost;
    private SshClient sshClient;
    private ClientSession session;
    private SftpClient sftpClient;

    public MinaSftpClient(String targetHost) {
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
        sftpClient = SftpClientFactory.instance().createSftpClient(session);
        logger.info("Connected to SFTP server {}",  targetHost);
    }

    public SshClient getSshClient() {
        return sshClient;
    }

    public void close() throws IOException {
        sftpClient.close();
        session.close();
        sshClient.close();
    }
}

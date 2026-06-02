package com.github.thomasandre84.sftp.channel;

import com.github.thomasandre84.sftp.session.MinaSshSession;
import org.apache.sshd.sftp.client.SftpClient;

import java.util.Objects;

/**
 * AutoCloseable wrapper for an SFTP client bound to a MinaSshSession.
 * When closed it notifies the manager so the internal counter is decremented
 * and the session can be returned to the pool when appropriate.
 */
public final class ManagedSftpClient implements AutoCloseable {
    private final SftpChannelManager manager;
    private final MinaSshSession sshSession;
    private final SftpClient sftpClient;
    private volatile boolean closed = false;

    public ManagedSftpClient(SftpChannelManager manager, MinaSshSession sshSession, SftpClient sftpClient) {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.sshSession = Objects.requireNonNull(sshSession, "sshSession");
        this.sftpClient = Objects.requireNonNull(sftpClient, "sftpClient");
    }

    public MinaSshSession getSshSession() {
        return sshSession;
    }

    public SftpClient getSftpClient() {
        return sftpClient;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        manager.returnSftpClient(this);
    }
}


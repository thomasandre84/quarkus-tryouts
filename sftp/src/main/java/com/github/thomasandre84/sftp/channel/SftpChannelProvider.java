package com.github.thomasandre84.sftp.channel;

import com.github.thomasandre84.sftp.session.MinaSshSession;
import com.github.thomasandre84.sftp.session.SshSessionPoolManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provider for SSH channels. Tracks number of open channels per session without using reflection
 * by keeping an application-level counter that is incremented when creating a client and
 * decremented when the wrapper is closed.
 */
@ApplicationScoped
public class SftpChannelProvider {
    private static final int MAX_CHANNELS_PER_SESSION = 2;
    private final SshSessionPoolManager sshSessionPoolManager;

    // track sessions and how many channels are currently in use for each
    private final ConcurrentHashMap<MinaSshSession, AtomicInteger> channelCount = new ConcurrentHashMap<>();

    @Inject
    public SftpChannelProvider(SshSessionPoolManager sshSessionPoolManager) {
        this.sshSessionPoolManager = sshSessionPoolManager;
    }

    /**
     * Borrow (or reuse) a MinaSshSession for the given host and create an SftpClient bound to it.
     * The returned SessionChannel MUST be closed by the caller to decrement the counter and
     * potentially return the session to the pool.
     */
    public SessionChannel getSftpClient(String host) throws Exception {
        MinaSshSession minaSession = getSession(host);
        // create SFTP client on the underlying ClientSession
        SftpClient sftpClient = SftpClientFactory.instance().createSftpClient(minaSession.getSession());
        // increment channel count
        channelCount.computeIfAbsent(minaSession, k -> new AtomicInteger(0)).incrementAndGet();
        return new SessionChannel(minaSession, sftpClient);
    }

    /**
     * Return a client. This will be called from SessionChannel.close() and will handle
     * decrementing the counter and returning the session to the pool if no channels remain.
     */
    private void returnSftpClient(SessionChannel sessionChannel) {
        Objects.requireNonNull(sessionChannel, "sessionChannel");
        MinaSshSession session = sessionChannel.sshSession();
        String host = session.getKey();

        // close the sftp client (already closed by SessionChannel.close usually, but be defensive)
        try {
            sessionChannel.sftpClient().close();
        } catch (IOException e) {
            // ignore or log if you have a logger
        }

        AtomicInteger cnt = channelCount.get(session);
        if (cnt != null) {
            int remaining = cnt.decrementAndGet();
            if (remaining <= 0) {
                // remove mapping and return session to pool
                channelCount.remove(session);
                try {
                    sshSessionPoolManager.returnClient(host, session);
                } catch (Exception e) {
                    // ignore or log
                }
            }
        } else {
            // no counter found -> defensive return
            try {
                sshSessionPoolManager.returnClient(host, session);
            } catch (Exception e) {
                // ignore or log
            }
        }
    }

    /**
     * Find an existing session with available channel capacity or borrow a new one.
     */
    private MinaSshSession getSession(String host) throws Exception {
        // try to reuse an existing session with available channel slots
        for (var entry : channelCount.entrySet()) {
            MinaSshSession sess = entry.getKey();
            AtomicInteger cnt = entry.getValue();
            // Note: compare by host if your MinaSshSession carries the host; otherwise adapt
            if (sess.getKey().equals(host) && cnt.get() < MAX_CHANNELS_PER_SESSION && sess.getSession().isOpen()) {
                return sess;
            }
        }

        // no reusable session found, borrow a new one from the pool
        MinaSshSession newSession = sshSessionPoolManager.borrowClient(host);
        channelCount.putIfAbsent(newSession, new AtomicInteger(0));
        return newSession;
    }

}

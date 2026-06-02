package com.github.thomasandre84.sftp.lock;

import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.sql.Timestamp;

/**
 * Simple DB lease/lock implementation using a lock table. Designed to keep the
 * critical DB section short: acquire lock via fast SQL, COMMIT, then perform
 * long-running SFTP operation outside DB transaction. Release the lock afterwards.
 *
 * NOTE: This implementation assumes a relational DB with standard SQL and that
 * a table `sftp_locks(resource text primary key, owner text, expires_at timestamptz)`
 * exists. Adjust DDL for your RDBMS.
 */
@ApplicationScoped
public class LockService {
    private final DataSource dataSource;

    @Inject
    public LockService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Try to acquire a lock for the given resource. Returns true if acquired.
     * ttl is the requested lease duration.
     */
    public boolean acquireLock(String resource, String owner, Duration ttl) throws SQLException {
        Instant expiresAt = Instant.now().plus(ttl);
        String insertSql = "INSERT INTO sftp_locks(resource, owner, expires_at) VALUES (?, ?, ?)";
        try (Connection c = dataSource.getConnection()) {
            // Try INSERT first - if it succeeds we have the lock
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setString(1, resource);
                ps.setString(2, owner);
                ps.setTimestamp(3, Timestamp.from(expiresAt));
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                // Insert failed - possibly conflict. Fall through to try conditional update.
            }

            // Try to take over expired lock or re-acquire if we are the owner
            String updateSql = "UPDATE sftp_locks SET owner = ?, expires_at = ? WHERE resource = ? AND (expires_at < now() OR owner = ?)";
            try (PreparedStatement ps2 = c.prepareStatement(updateSql)) {
                ps2.setString(1, owner);
                ps2.setTimestamp(2, Timestamp.from(expiresAt));
                ps2.setString(3, resource);
                ps2.setString(4, owner);
                int updated = ps2.executeUpdate();
                return updated > 0;
            }
        }
    }

    /**
     * Release the lock if owned by the given owner. Safe to call even if not owner.
     */
    public void releaseLock(String resource, String owner) throws SQLException {
        String deleteSql = "DELETE FROM sftp_locks WHERE resource = ? AND owner = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(deleteSql)) {
            ps.setString(1, resource);
            ps.setString(2, owner);
            ps.executeUpdate();
        }
    }

    /**
     * Optional: refresh the lock TTL if still owner.
     */
    public boolean refreshLock(String resource, String owner, Duration ttl) throws SQLException {
        Instant expiresAt = Instant.now().plus(ttl);
        String refreshSql = "UPDATE sftp_locks SET expires_at = ? WHERE resource = ? AND owner = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(refreshSql)) {
            ps.setTimestamp(1, Timestamp.from(expiresAt));
            ps.setString(2, resource);
            ps.setString(3, owner);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }
}


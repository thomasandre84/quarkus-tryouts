package com.github.thomasandre84.sftp.db;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "sftp_lock")
public class SftpLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private Instant lockTime;

    private Instant expireTime;

    private String targetHost;

    private String lockingHost;

    protected SftpLock() {
    }

    public SftpLock(Instant lockTime, Instant expireTime, String targetHost, String lockingHost) {
        this.lockTime = lockTime;
        this.expireTime = expireTime;
        this.targetHost = targetHost;
        this.lockingHost = lockingHost;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Instant getLockTime() {
        return lockTime;
    }

    public void setLockTime(Instant lockTime) {
        this.lockTime = lockTime;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getLockingHost() {
        return lockingHost;
    }

    public void setLockingHost(String lockingHost) {
        this.lockingHost = lockingHost;
    }

    @Override
    public String toString() {
        return "SftpLock{" +
                "id=" + id +
                ", version=" + version +
                ", lockTime=" + lockTime +
                ", expireTime=" + expireTime +
                ", targetHost='" + targetHost + '\'' +
                ", lockingHost='" + lockingHost + '\'' +
                '}';
    }
}

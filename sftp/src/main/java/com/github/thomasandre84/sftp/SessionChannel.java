package com.github.thomasandre84.sftp;

import org.apache.sshd.sftp.client.SftpClient;

public record SessionChannel(MinaSshSession sshSession, SftpClient sftpClient) {
}

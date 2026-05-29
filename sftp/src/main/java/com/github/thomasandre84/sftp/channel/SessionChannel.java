package com.github.thomasandre84.sftp.channel;

import com.github.thomasandre84.sftp.session.MinaSshSession;
import org.apache.sshd.sftp.client.SftpClient;

public record SessionChannel(MinaSshSession sshSession, SftpClient sftpClient) {
}

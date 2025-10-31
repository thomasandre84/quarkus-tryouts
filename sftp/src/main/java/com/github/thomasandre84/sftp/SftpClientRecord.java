package com.github.thomasandre84.sftp;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;

public record SftpClientRecord(SshClient sshClient, ClientSession session, SftpClient sftpClient) {
}

package com.github.thomasandre84.sftp.channel;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

@ApplicationScoped
public class SftpChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpChannelService.class);
    private static final ExecutorService executorService = newFixedThreadPool(10);

    public List<String> listDirAsync(String host) {
        LOGGER.info("List folder");
        return listFolder(host);
    }

    public void listDirAsync(String host, int amount) {

    }

    private List<String> listFolder(String host) {
        List<String> dirs = new ArrayList<>();

        return dirs;
    }
}

package io.github.thinkframework.dfs.backup.node.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thinkframework.dfs.backup.node.management.FSImageSocketChannel;
import io.github.thinkframework.dfs.commons.config.ApplicationConfiguration;
import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.management.NameNodeService;
import io.github.thinkframework.dfs.rpc.FsImages;
import io.github.thinkframework.dfs.rpc.FsImagesResourceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

public class FSImageService {

    private static final Logger logger = LoggerFactory.getLogger(FSImageService.class);

    private ApplicationConfiguration applicationConfiguration;

    private NameNodeService nameNodeService;

    private Timer flushTimer;


    private Path fsImagesPath;

    private Path checkpointPath;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public FSImageService(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public void start() {
        fsImagesPath = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir() + File.separator
                + "fsImages" + File.separator
                + "fsImage.txt");

        checkpointPath = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + applicationConfiguration.getWorkdir() + File.separator
                + "checkpoint.txt");

        if(Files.notExists(fsImagesPath)) {
            try {
                Files.createFile(fsImagesPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        flushTimer = new Timer("刷盘",true);
        flushTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(nameNodeService.getCheckpoint().equals(String.valueOf(nameNodeService.getEditLogsId()))) { // 启动后没有新的editLogs\
                    return;
                }
                flush();
                logger.debug("刷盘成功.");
            }
        },10000L,10000L);
    }

    /**
     * 刷磁盘
     */
    private void flush(){
        try {
            Files.writeString(fsImagesPath,gson.toJson(nameNodeService.getRoot()));
            Files.writeString(checkpointPath,String.valueOf(nameNodeService.getEditLogsId()));
            nameNodeService.setCheckpoint(String.valueOf(nameNodeService.getEditLogsId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NameNodeService getNameNodeService() {
        return nameNodeService;
    }

    public void setNameNodeService(NameNodeService nameNodeService) {
        this.nameNodeService = nameNodeService;
    }
}

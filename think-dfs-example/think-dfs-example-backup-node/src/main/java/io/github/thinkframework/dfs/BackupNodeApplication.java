package io.github.thinkframework.dfs;

import io.github.thinkframework.dfs.backup.node.config.BackupNodeConfiguration;
import io.github.thinkframework.dfs.backup.node.management.FSImageSocketChannel;
import io.github.thinkframework.dfs.backup.node.server.BackupNodeServer;
import io.github.thinkframework.dfs.backup.node.service.FSImageService;
import io.github.thinkframework.dfs.commons.management.NameNodeService;

import java.io.IOException;

public class BackupNodeApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 节点
        NameNodeService nameNodeService = new NameNodeService();
        nameNodeService.setApplicationConfiguration(new BackupNodeConfiguration());
        nameNodeService.start(); // 加载

        FSImageService fsImageService = new FSImageService(new BackupNodeConfiguration());
        fsImageService.setNameNodeService(nameNodeService);
        fsImageService.start(); // 保存

        FSImageSocketChannel fsImageSocketChannel = new FSImageSocketChannel();
        fsImageService.setNameNodeService(nameNodeService);
        fsImageSocketChannel.setFsImageService(fsImageService);
        fsImageSocketChannel.start(); // 同步fsimage

        BackupNodeServer backUpNodeServer = new BackupNodeServer();
        backUpNodeServer.setApplicationConfiguration(new BackupNodeConfiguration());
        backUpNodeServer.setNameNodeService(nameNodeService);
        backUpNodeServer.start(); // 拉取editlogs


        backUpNodeServer.awaitTermination();
    }
}

package io.github.thinkframework.dfs;

import io.github.thinkframework.dfs.name.node.NameNodeFileSystem;
import io.github.thinkframework.dfs.name.node.manager.ThinkFileLogManager;
import io.github.thinkframework.dfs.name.node.server.NameNodeServer;
import io.github.thinkframework.dfs.name.node.service.NameNodeFileSystemService;
import io.github.thinkframework.dfs.name.node.service.NameNodeServiceRegistry;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class NameNodeApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        NameNodeServer nameNodeServer = new NameNodeServer();
        nameNodeServer.setNameNodeServiceRegistry(new NameNodeServiceRegistry());

        ThinkFileLogManager fileLogManager = new ThinkFileLogManager();
        NameNodeFileSystem nameNodeFileSystem =new NameNodeFileSystem();
        nameNodeFileSystem.setFileLogManager(fileLogManager);
        NameNodeFileSystemService nameNodeFileSystemService = new NameNodeFileSystemService();
        nameNodeFileSystemService.setNameNodeFileSystem(nameNodeFileSystem);

        nameNodeServer.setNameNodeFileSystemService(nameNodeFileSystemService);

        nameNodeServer.start();
        nameNodeServer.awaitTermination();
    }

}

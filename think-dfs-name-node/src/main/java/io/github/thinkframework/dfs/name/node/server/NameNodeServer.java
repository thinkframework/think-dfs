package io.github.thinkframework.dfs.name.node.server;

import io.github.thinkframework.dfs.name.node.service.NameNodeServiceRegistry;
import io.github.thinkframework.dfs.name.node.service.NameNodeFileSystemService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class NameNodeServer {

    /**
     * 监听端口
     */
    public static final int PORT = 9001;

    private Server server;

    private NameNodeServiceRegistry nameNodeServiceRegistry;
    private NameNodeFileSystemService nameNodeFileSystemService;

    public NameNodeServer() {
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(nameNodeServiceRegistry)
                .addService(nameNodeFileSystemService)
                .build();
        server.start();
    }


    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }

    public NameNodeServiceRegistry getNameNodeServiceRegistry() {
        return nameNodeServiceRegistry;
    }

    public void setNameNodeServiceRegistry(NameNodeServiceRegistry nameNodeServiceRegistry) {
        this.nameNodeServiceRegistry = nameNodeServiceRegistry;
    }

    public NameNodeFileSystemService getNameNodeFileSystemService() {
        return nameNodeFileSystemService;
    }

    public void setNameNodeFileSystemService(NameNodeFileSystemService nameNodeFileSystemService) {
        this.nameNodeFileSystemService = nameNodeFileSystemService;
    }
}

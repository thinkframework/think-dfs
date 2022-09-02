package io.github.thinkframework.dfs.name.node.server;

import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.name.node.web.rest.EditLogsResource;
import io.github.thinkframework.dfs.name.node.web.rest.FileSystemResource;
import io.github.thinkframework.dfs.name.node.web.rest.ServiceRegistryResources;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 *
 */
public class NameNodeServer {

    private Server server;

    private ServiceRegistryResources serviceRegistryResources;

    private EditLogsResource editLogsResource;

    private FileSystemResource fileSystemResource;

    public NameNodeServer() {
    }

    public void start() throws IOException {
        server = ServerBuilder
                .forPort(Constants.NAME_NODE_PORT)
                .addService(serviceRegistryResources)
                .addService(editLogsResource)
                .addService(fileSystemResource)
                .build();
        server.start();
    }


    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }

    public ServiceRegistryResources getNameNodeServiceRegistry() {
        return serviceRegistryResources;
    }

    public void setNameNodeServiceRegistry(ServiceRegistryResources serviceRegistryResources) {
        this.serviceRegistryResources = serviceRegistryResources;
    }

    public EditLogsResource getFileLogsResources() {
        return editLogsResource;
    }

    public void setFileLogsResources(EditLogsResource editLogsResource) {
        this.editLogsResource = editLogsResource;
    }

    public FileSystemResource getNameNodeFileSystemService() {
        return fileSystemResource;
    }

    public void setNameNodeFileSystemService(FileSystemResource fileSystemResource) {
        this.fileSystemResource = fileSystemResource;
    }
}

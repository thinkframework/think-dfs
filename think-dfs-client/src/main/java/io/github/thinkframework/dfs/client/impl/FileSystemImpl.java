package io.github.thinkframework.dfs.client.impl;

import io.github.thinkframework.dfs.client.DataNodeSocketChannel;
import io.github.thinkframework.dfs.client.FileSystem;
import io.github.thinkframework.dfs.commons.discovery.DiscoveryClient;
import io.github.thinkframework.dfs.commons.discovery.ServiceInstance;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileSystemImpl implements FileSystem {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemImpl.class);

    /**
     * 监听端口
     */
    public static final int PORT = 9001;

    ThinkFileSystemGrpc.ThinkFileSystemBlockingStub fileSystemBlockingStub;

    DataNodeSocketChannel dataNodeSocketChannel;

    public DiscoveryClient discoveryClient;

    public FileSystemImpl(){
        fileSystemBlockingStub = ThinkFileSystemGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost",PORT)
                        .usePlaintext()
                        .build());
    }

    @Override
    public void mkdir(String path){
        fileSystemBlockingStub.mkdir(
                io.github.thinkframework.dfs.rpc.File.newBuilder()
                .setPath(path)
                .build());
    }

    public void send(File file) throws IOException {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("dataNode");

        serviceInstances.forEach(serviceInstance -> {
            new DataNodeSocketChannel(file).run();
        });

    }

}

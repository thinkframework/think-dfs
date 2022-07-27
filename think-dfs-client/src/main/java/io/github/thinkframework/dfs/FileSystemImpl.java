package io.github.thinkframework.dfs;

import io.github.thinkframework.dfs.rpc.File;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.ManagedChannelBuilder;

public class FileSystemImpl implements FileSystem{

    /**
     * 监听端口
     */
    public static final int PORT = 9001;

    ThinkFileSystemGrpc.ThinkFileSystemBlockingStub stub;

    public FileSystemImpl(){
        stub = ThinkFileSystemGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost",PORT)
                        .usePlaintext()
                        .build());
    }

    @Override
    public void mkdir(String path){
        stub.mkdir(File.newBuilder()
                .setPath(path)
                .build());
    }
}

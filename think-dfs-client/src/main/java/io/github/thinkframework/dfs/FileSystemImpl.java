package io.github.thinkframework.dfs;

import io.github.thinkframework.dfs.rpc.File;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.ManagedChannelBuilder;

public class FileSystemImpl {
    ThinkFileSystemGrpc.ThinkFileSystemBlockingStub stub;
    public FileSystemImpl(){
        stub = ThinkFileSystemGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost",9001)
                        .usePlaintext()
                        .build());
    }

    void mkdir(String path){
        stub.mkdir(File.newBuilder().setPath("abc").build());
    }
}

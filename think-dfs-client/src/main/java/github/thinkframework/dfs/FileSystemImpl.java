package github.thinkframework.dfs;

import io.grpc.ManagedChannelBuilder;
import io.thinkframework.dfs.rpc.File;
import io.thinkframework.dfs.rpc.ThinkFileSystemGrpc;

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

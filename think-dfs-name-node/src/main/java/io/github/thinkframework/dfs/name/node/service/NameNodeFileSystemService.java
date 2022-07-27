package io.github.thinkframework.dfs.name.node.service;


import io.github.thinkframework.dfs.name.node.NameNodeFileSystem;
import io.github.thinkframework.dfs.rpc.File;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.stub.StreamObserver;

public class NameNodeFileSystemService extends ThinkFileSystemGrpc.ThinkFileSystemImplBase {

    private NameNodeFileSystem nameNodeFileSystem;

    @Override
    public void mkdir(File request, StreamObserver<File> responseObserver) {
        nameNodeFileSystem.mkdir(request.getPath());
        File response = request;
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public NameNodeFileSystem getNameNodeFileSystem() {
        return nameNodeFileSystem;
    }

    public void setNameNodeFileSystem(NameNodeFileSystem nameNodeFileSystem) {
        this.nameNodeFileSystem = nameNodeFileSystem;
    }
}

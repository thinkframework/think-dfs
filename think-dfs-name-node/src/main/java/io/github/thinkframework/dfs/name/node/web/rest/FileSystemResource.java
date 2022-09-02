package io.github.thinkframework.dfs.name.node.web.rest;


import io.github.thinkframework.dfs.name.node.management.FileSystemService;
import io.github.thinkframework.dfs.rpc.File;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * 文件系统
 */
public class FileSystemResource extends ThinkFileSystemGrpc.ThinkFileSystemImplBase {

    private FileSystemService fileSystemService;

    @Override
    public void mkdir(File request, StreamObserver<File> responseObserver) {
        try {
            fileSystemService.mkdir(request.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File response = request;
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public FileSystemService getNameNodeFileSystem() {
        return fileSystemService;
    }

    public void setNameNodeFileSystem(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }
}

package github.thinkframework.dfs.data.node.service;

import io.github.thinkframework.dfs.rpc.File;
import io.github.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateNodeFileSystemService extends ThinkFileSystemGrpc.ThinkFileSystemImplBase {
    private static final Logger logger = LoggerFactory.getLogger(DateNodeFileSystemService.class);
    @Override
    public void mkdir(File request, StreamObserver<File> responseObserver) {
        logger.info(request.getPath());
//        super.mkdir(request, responseObserver);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }
}

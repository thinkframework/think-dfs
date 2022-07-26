package github.thinkframework.dfs;

import io.grpc.stub.StreamObserver;
import io.thinkframework.dfs.rpc.File;
import io.thinkframework.dfs.rpc.ThinkFileSystemGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThinkFileSystem extends ThinkFileSystemGrpc.ThinkFileSystemImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ThinkFileSystem.class);
    @Override
    public void mkdir(File request, StreamObserver<File> responseObserver) {
        logger.info(request.getPath());
//        super.mkdir(request, responseObserver);
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }
}

package github.thinkframework.dfs.data.node.server;

import io.github.thinkframework.dfs.ThinkServiceRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class DataNodeServer {

    /**
     * 监听端口
     */
    public static final int PORT = 9002;

    private Server server;

    public DataNodeServer() {
        server = ServerBuilder.forPort(PORT)
                .addService(new ThinkServiceRegistry())
                .build();
    }

    public void start() throws IOException {
        server.start();
    }


    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }
}

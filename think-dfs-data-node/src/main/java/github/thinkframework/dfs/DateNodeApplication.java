package github.thinkframework.dfs;

import github.thinkframework.dfs.data.node.server.DataNodeServer;
import io.github.thinkframework.dfs.ThinkServiceRegistry;

import java.io.IOException;

public class DateNodeApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        DataNodeServer dataNodeServer = new DataNodeServer();
        dataNodeServer.start();
        dataNodeServer.awaitTermination();
    }
}

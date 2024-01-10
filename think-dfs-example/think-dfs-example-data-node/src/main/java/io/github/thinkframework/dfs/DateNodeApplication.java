package io.github.thinkframework.dfs;

import github.thinkframework.dfs.data.node.server.DataNodeSocketChannelServer;

import java.io.IOException;

public class DateNodeApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        DataNodeSocketChannelServer server =new DataNodeSocketChannelServer();
        server.start();
    }
}

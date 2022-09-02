package io.github.thinkframework.dfs.commons;

import io.github.thinkframework.dfs.commons.server.Server;

public class BootStrap {

    private Server server;

    public void start(){
        server.start();
    }

    public void await() {

    }

    public void stop() {
        server.stop();
    }
}

package io.github.thinkframework.dfs.commons.server;

public class AbstractServer implements Server{

    private Thread awaitThread;

    private volatile boolean running;

    @Override
    public void start() {
        awaitThread = Thread.currentThread();
        running = true;

    }

    @Override
    public void stop() {
        running = false;
        awaitThread.interrupt();
    }

    @Override
    public void await() {

    }
}

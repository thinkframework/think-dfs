package github.thinkframework.dfs.data.node.server;


import io.github.thinkframework.dfs.commons.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class DataNodeSocketChannelServer {

    private static final Logger logger = LoggerFactory.getLogger(DataNodeSocketChannelServer.class);


    private static final int count = 1;

    private volatile boolean running;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private static ExecutorService connectorService = Executors.newFixedThreadPool(count);

    private static ExecutorService executorService = Executors.newFixedThreadPool(count);

    private List<Processor> processors = new CopyOnWriteArrayList<>();

    private static Selector selector;

    public void init() {

    }

    public void start() throws IOException {
        running = true;
        selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        // 监听3次握手
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ServerSocket serverSocket = serverSocketChannel.socket();
        // 监听端口
        serverSocket.bind(new InetSocketAddress("localhost", Constants.DATA_NODE_PORT));

        for (int i=0;i<count;i++) {
            Processor processor = new Processor();
            processors.add(processor);
            executorService.submit(processor);
        }

        connectorService.submit(() -> {
            while(running){
                int count = 0;
                try {
                    count = selector.select();
                } catch (IOException e) {
                    logger.error("", e);
                }
                if(count > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    if(selectionKeys != null) {
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                            while (iterator.hasNext()) {
                                SelectionKey selectionKey = iterator.next();
                                iterator.remove();
                                if(selectionKey.isAcceptable()){
                                    try {
                                        SocketChannel socketChannel =  accept(selectionKey);
                                        processors.stream().findFirst().get().add(socketChannel); // todo 优化,现在是单线程
                                    } catch (IOException e) {
                                        logger.error("接受请求异常", e);
                                    }
                                }
                            }
                    }
                }

            }
        });
    }

    /**
     * 接受请求
     * @param selectionKey
     * @throws IOException
     */
    private SocketChannel accept(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = null;
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                logger.debug("链接成功");
            }
        } finally {
            return socketChannel;
        }
    }

}

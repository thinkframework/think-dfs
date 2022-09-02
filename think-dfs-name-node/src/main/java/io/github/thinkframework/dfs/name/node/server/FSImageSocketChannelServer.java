package io.github.thinkframework.dfs.name.node.server;


import io.github.thinkframework.dfs.commons.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class FSImageSocketChannelServer {

    private static final Logger logger = LoggerFactory.getLogger(FSImageSocketChannelServer.class);

    private Path fsImagePath = Paths.get(System.getProperty("user.home") + File.separator
            + "tmp" + File.separator
            + "nameNode" + File.separator
            + "fsImages"  + File.separator
            + "fsImage.txt");

    private Path editLogsPath = Paths.get(System.getProperty("user.home") + File.separator
            + "tmp" + File.separator
            + "nameNode" + File.separator
            + "editLogs");

    private static final int count = 1;

    private volatile boolean running;

    private static ExecutorService executorService = Executors.newFixedThreadPool(count);

    private static Selector selector;

    public void start() {
        new Thread(() -> {
            try{
                running = true;
                selector = Selector.open();

                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                // 监听3次握手
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                ServerSocket serverSocket = serverSocketChannel.socket();
                // 监听端口
                serverSocket.bind(new InetSocketAddress("localhost", Constants.FS_IMAGE_PORT));

                while (running) {
                    int count = selector.select();
                    if (count > 0) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        if (selectionKeys != null) {
                            Iterator<SelectionKey> iterator = selectionKeys.iterator();
                            while (iterator.hasNext()) {
                                SelectionKey selectionKey = iterator.next();
                                iterator.remove();
                                new Worker(selectionKey).run();
//                                executorService.submit(new Worker(iterator.next()));
                            }
                        }
                    }
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class Worker implements Runnable {

        SelectionKey selectionKey;

        public Worker() {
        }

        public Worker(SelectionKey selectionKey) {
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            try {
                if (selectionKey.isAcceptable()) {
                    accept(selectionKey);
                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                } else if (selectionKey.isWritable()) {
                    write(selectionKey);
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void accept(SelectionKey selectionKey) throws IOException {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            logger.info("链接成功");
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        private void read(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);

            RandomAccessFile randomAccessFile = new RandomAccessFile(fsImagePath.toFile(),"rw");
            for(int count;(count = socketChannel.read(byteBuffer)) > 0;byteBuffer.clear()){
                byteBuffer.flip();
                randomAccessFile.write(byteBuffer.array(),0,byteBuffer.limit());
            }
            randomAccessFile.close();
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        }

        private void write(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.write(ByteBuffer.wrap("fsImage更新成功".getBytes(StandardCharsets.UTF_8)));
            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);// 等待客户端关闭
            logger.info("fsImage更新成功");
        }

    }
}

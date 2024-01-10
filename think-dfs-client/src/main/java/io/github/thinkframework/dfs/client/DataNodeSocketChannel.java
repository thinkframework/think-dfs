package io.github.thinkframework.dfs.client;

import io.github.thinkframework.dfs.commons.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public class DataNodeSocketChannel implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(DataNodeSocketChannel.class);
    static Selector selector;

    private File file;

    private volatile boolean finish;

    public DataNodeSocketChannel(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", Constants.DATA_NODE_PORT));

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);


            while (!finish) {
                int count = selector.select();
                if (count > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    if (selectionKeys != null) {
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();
                            if (selectionKey.isConnectable()) {
                                new Worker(selectionKey).connect(selectionKey);
                            } else {
                                new Worker(selectionKey).run();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("",e);
        }
    }


    class Worker implements Runnable {
        SelectionKey selectionKey;

        public Worker(SelectionKey selectionKey) {
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            try {
                 if (selectionKey.isWritable()) {
                    write(selectionKey);
                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                }
            } catch (ClosedChannelException e) {
                finish = true; // fixme 先这么处理
                e.printStackTrace();
            } catch (IOException e) {
                finish = true; // fixme 先这么处理
                e.printStackTrace();
            } catch (Exception e) {
                finish = true; // fixme 先这么处理
                e.printStackTrace();
            }
        }


        private void connect(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.configureBlocking(false);
            if(socketChannel.isConnectionPending()){
                while(!socketChannel.finishConnect()){
                }
            }
            logger.debug("链接成功");
            // 取消链接事件的关注
            selectionKey.interestOps(selectionKey.interestOps() &(~SelectionKey.OP_CONNECT));
            // 关注写事件
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        }

        private void read(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
            socketChannel.read(byteBuffer);
            logger.info("{}",new String(byteBuffer.array()));
            finish = true;
            socketChannel.close();
        }

        private void write(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            try {
                if(file.exists()){
                    ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE * 2);
                    byte[] fileName =  file.getName().getBytes(StandardCharsets.UTF_8);
                    // 文件名
                    byteBuffer.putInt(fileName.length);
                    byteBuffer.put(ByteBuffer.wrap(fileName));
                    // 文件大小
                    byteBuffer.putLong(file.length());
                    byteBuffer.put(ByteBuffer.wrap(Files.readAllBytes(file.toPath())));
                    byteBuffer.flip();
                    // 文件
                    socketChannel.write(byteBuffer);
                    byteBuffer.clear();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketChannel.register(selector, SelectionKey.OP_READ);

            logger.info("等待响应");
        }
    }
}

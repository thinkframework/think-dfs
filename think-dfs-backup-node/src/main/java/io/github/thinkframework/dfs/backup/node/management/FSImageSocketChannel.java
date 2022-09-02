package io.github.thinkframework.dfs.backup.node.management;

import io.github.thinkframework.dfs.backup.node.service.FSImageService;
import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.management.NameNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

public class FSImageSocketChannel{

    private static final Logger logger = LoggerFactory.getLogger(FSImageSocketChannel.class);

    private Path path = Paths.get(System.getProperty("user.home") + File.separator
            + "tmp" + File.separator
            + "backupNode" + File.separator
            + "fsImages" + File.separator
            + "fsImage.txt");

    private Selector selector;

    private NameNodeService nameNodeService;

    private FSImageService fsImageService;

    public void start() {
        Thread thread = new Thread(() -> {
            try {
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress("localhost", Constants.FS_IMAGE_PORT));

                selector = Selector.open();
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                while (true) {
                    int count = selector.select();
                    if (count > 0) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        if (selectionKeys != null) {
                            Iterator<SelectionKey> iterator = selectionKeys.iterator();
                            while (iterator.hasNext()) {
                                SelectionKey selectionKey = iterator.next();
                                iterator.remove();
                                new Worker(selectionKey)
                                        .run();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("",e);
            }
        });
        thread.start();
    }


    class Worker implements Runnable {
        SelectionKey selectionKey;

        public Worker(SelectionKey selectionKey) {
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            try {
                if (selectionKey.isConnectable()) {
                   connect(selectionKey);
                }  else if (selectionKey.isReadable()) {
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


        private void connect(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.configureBlocking(false);
            if(socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
            }
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        }

        private void read(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer response = ByteBuffer.allocate(Constants.BUFFER_SIZE);
            socketChannel.read(response);
            response.flip();
            if(response.hasRemaining()) { // 读取到数据
                logger.info("响应: {}", new String(response.array(), 0, response.limit()));
                socketChannel.close();
                try { // TODO 不要在此处暂停，抽出去
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void write(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            File file = path.toFile();
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(),"rw")) {
                    FileChannel fileChannel = randomAccessFile.getChannel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(((Long)file.length()).intValue());
                    for(int count;(count = fileChannel.read(byteBuffer)) > 0;byteBuffer.clear()){
                        byteBuffer.flip();
                        socketChannel.write(byteBuffer);
                    }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    public NameNodeService getNameNodeService() {
        return nameNodeService;
    }

    public void setNameNodeService(NameNodeService nameNodeService) {
        this.nameNodeService = nameNodeService;
    }

    public FSImageService getFsImageService() {
        return fsImageService;
    }

    public void setFsImageService(FSImageService fsImageService) {
        this.fsImageService = fsImageService;
    }
}

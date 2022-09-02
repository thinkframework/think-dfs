package github.thinkframework.dfs.data.node.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private LinkedBlockingQueue<SocketChannel> queue;

    private ConcurrentHashMap<SocketChannel,ConcurrentHashMap<String,ByteBuffer>> cachedByteBuffer = new ConcurrentHashMap<>();
    private ConcurrentHashMap<SocketChannel,ConcurrentHashMap<String,Object>> cachedObject = new ConcurrentHashMap<>();

    private static Selector selector;
    public Processor() throws IOException {
        selector = Selector.open();
        this.queue = new LinkedBlockingQueue<>();
    }

    SelectionKey selectionKey;

    public void add(SocketChannel socketChannel){
        queue.add(socketChannel);
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) { // todo 优雅关闭
            try {
                register();
                poll();
            } catch (ClosedChannelException e) {
                logger.error("链接关闭异常", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * 注册监听
     * @throws ClosedChannelException
     */
    public void register() throws ClosedChannelException {
        SocketChannel socketChannel;
        while((socketChannel = queue.poll()) != null){
            socketChannel.register(selector,SelectionKey.OP_READ);
            logger.debug("等待输入流");
        }
    }

    public void poll() throws IOException {
        int count = selector.select(1000L);
        if (count > 0) {
            Set<SelectionKey> selectionKeys =  selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if(selectionKey.isReadable()) {
                    logger.debug("获取到输入流");
                    read(selectionKey);
                }
                if(selectionKey.isWritable()) {
                    logger.debug("获取到输出流");
                    write(selectionKey);
                }
            }
        }
    }

    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        Object object = null;
        Integer length = null;
        String fileName = null;
        byte[] file = null;
        if ((object = readCache(socketChannel, "fileNameLength", 4)) != null) {
            if (object instanceof ByteBuffer) {
            } else {
                length = (Integer) object;
            }
        } else {
            return; // 拆包
        }

        if ((object = readCache(socketChannel, "fileName", length)) != null) {
            if (object instanceof ByteBuffer) {
            } else {
                fileName = (String) object;
            }
        } else {
            return; // 拆包
        }
        if ((object = readCache(socketChannel, "fileLength", 8)) != null) {
            if (object instanceof ByteBuffer) {
            } else {
                length = (Integer) object;
            }
        } else {
            return; // 拆包
        }

        if ((object = readCache(socketChannel, "file", length)) != null) {
            if (object instanceof ByteBuffer) {
            } else {
                file = (byte[]) object;
            }
        } else {
            return; // 拆包
        }

        Path path = Paths.get(System.getProperty("user.home") + File.separator
                + "tmp" + File.separator
                + "dataNode" + File.separator
                + "files" + File.separator
                + fileName);
        Files.write(path, file);
        logger.info("文件名称:{},文件大小:{}.", new String(file),length);

        selectionKey.interestOps(SelectionKey.OP_WRITE);

        logger.info("等待响应");
    }

    private void write(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.write(ByteBuffer.wrap("读取成功".getBytes(StandardCharsets.UTF_8)));
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        logger.info("等待关闭");
    }



    private Object readCache(SocketChannel socketChannel, String key,int size) throws IOException {
        Object object;
        if((object = cachedObject.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,Object>()).get(key)) != null){
            return object;
        }
        ByteBuffer buffer;
        if((buffer = cachedByteBuffer.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,ByteBuffer>()).get(key)) != null){

        } else {
            buffer = ByteBuffer.allocate(size);
        }

        socketChannel.read(buffer);
        if(buffer.hasRemaining()){
            // 缓存未填满表示发生拆包
            ConcurrentHashMap map = cachedByteBuffer.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,ByteBuffer>());
            map.put(key,buffer);
            return null;
        }


        buffer.flip();
        int length = 0; // todo 先写死,再扩展
        if ("fileNameLength".equals(key)) {
            length = buffer.getInt();
            cachedObject.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,Object>())
                    .put(key,length);
            return length;
        }
        if ("fileName".equals(key)) {
            String fileName = new String(buffer.array());
            cachedObject.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,Object>())
                    .put(key,fileName);
            return fileName;
        }
        if ("fileLength".equals(key)) {
            length = ((Long)buffer.getLong()).intValue();
            cachedObject.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,Object>())
                    .put(key,length);
            return length;
        }
        if ("file".equals(key)) {
            byte[] file = buffer.array();
            cachedObject.computeIfAbsent(socketChannel, k -> new ConcurrentHashMap<String,Object>())
                    .put(key,file);
            return file;
        }
        return buffer;
    }
}

package io.github.thinkframework.dfs.name.node.manager;

import io.github.thinkframework.dfs.name.node.NameNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.stream.Collectors;

public class ThinkFileLogManager {

    private static final Logger logger = LoggerFactory.getLogger(ThinkFileLogManager.class);

    DoubleBuffer buffer;

    private long txIdSeq = 0L;
    /**
     * 已同步的TxId
     */
    private volatile long syncTxId = 0L;

    /***
     * 正在刷盘
     */
    private volatile boolean syncRunning = false;

    /**
     *
     */
    private volatile boolean syncScheduling = false;

    private ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 必须加锁
     * @param node
     */
    public  void write(NameNode node) {
        synchronized(this) {
            try {
                waitSyncSchedule();
            } catch (InterruptedException e) {
                logger.error("waitSyncSchedule",e);
            }
            long txId = txIdSeq++;
            threadLocal.set(txId);
            logger.debug("{}", node.recursion(node).stream().collect(Collectors.joining(File.separator)));
            buffer.write();
            if (!buffer.shouldSyncToDisk()) { // 不需要写磁盘
                return; //退出
            }
            syncScheduling = true; // 同步调度
        }
        sync();
    }

    public synchronized void sync(){
        synchronized(this) {
            long txId = threadLocal.get();
            if (syncRunning){
            // todo 可能还有代码需要实现
            }
            // 交换缓冲区
            buffer.setReadyToSync();
            syncTxId = txId;
            syncScheduling = false;
            notifyAll(); // 唤醒正在等待缓冲区交换换成的线程
            // 准备刷盘
            syncRunning =true;
        }
        // 刷盘,另一个缓冲区不需要加锁
        buffer.flush();
        synchronized(this) {
            // 刷盘结束
            syncRunning = false;
            notifyAll();
        }
    }

    /**
     * 等待
     * @throws InterruptedException
     */
    public synchronized void waitSyncSchedule() throws InterruptedException {
        while (syncScheduling) {
            // 等待缓冲区交换完成
            wait(1000L);
        }
    }
}

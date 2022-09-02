package io.github.thinkframework.dfs.name.node.management;

import io.github.thinkframework.dfs.commons.domain.EditLog;
import io.github.thinkframework.dfs.name.node.log.DoubleBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EditLogsService {

    private static final Logger logger = LoggerFactory.getLogger(EditLogsService.class);

    private final long sleepTime = 1000L;

    private DoubleBuffer buffer;

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

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 必须加锁
     * @param editLog
     */
    public  void write(EditLog editLog) throws IOException {
        synchronized(this) {
            try {
                waitSyncSchedule();
            } catch (InterruptedException e) {
                logger.error("waitSyncSchedule",e);
            }
            long txId = txIdSeq++;
            editLog.setId(txId);
            threadLocal.set(txId);
            logger.debug("write: {}",editLog);
            buffer.write(editLog);
            if (!buffer.shouldSyncToDisk()) { // 不需要写磁盘
                return; //退出
            }
            syncScheduling = true; // 同步调度
        }

        sync();
    }

    public synchronized void sync() throws IOException {
        synchronized(this) {
            long txId = threadLocal.get();
            if (syncRunning){
                if(txId < syncTxId){
                    // 比同步任务小的线程不需要执行刷盘动作
                    return;
                }
                while (syncRunning){
                    try {
                        wait(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
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
        logger.info("刷盘");
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
            wait(sleepTime);
        }
    }

    public DoubleBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(DoubleBuffer buffer) {
        this.buffer = buffer;
    }

    public long getTxIdSeq() {
        return txIdSeq;
    }

    public void setTxIdSeq(long txIdSeq) {
        this.txIdSeq = txIdSeq;
    }

    public long getSyncTxId() {
        return syncTxId;
    }

    public void setSyncTxId(long syncTxId) {
        this.syncTxId = syncTxId;
    }
}

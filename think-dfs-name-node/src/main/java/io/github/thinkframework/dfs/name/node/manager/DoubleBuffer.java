package io.github.thinkframework.dfs.name.node.manager;

/***
 * 双缓冲
 */
public class DoubleBuffer {

    /**
     * 单块缓冲区大小,默认512字节
     */
    private Long LOG_BUFFER_LIMIT = 512L;

    private Long MaxTxId = 0L;

    /**
     * 写日志
     */
    FileLogBuffer currentBuffer;

    /**
     * 同步刷盘
     */
    FileLogBuffer syncBuffer;


    public void write(){
        currentBuffer.write();
    }

    public boolean shouldSyncToDisk(){
        if(currentBuffer.size() > LOG_BUFFER_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * 交换缓冲区
     * @return
     */
    public void setReadyToSync(){
        if(syncBuffer.size() == 0L) {
            FileLogBuffer temp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = currentBuffer;
        }
    }

    public void flush(){
        syncBuffer.flush();
        syncBuffer.clear();
    }
}

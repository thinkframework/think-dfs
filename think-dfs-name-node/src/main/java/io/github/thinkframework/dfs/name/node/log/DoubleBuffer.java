package io.github.thinkframework.dfs.name.node.log;

import io.github.thinkframework.dfs.commons.domain.EditLog;

import java.io.IOException;

/***
 * 双缓冲
 */
public class DoubleBuffer {

    /**
     * 单块缓冲区大小,默认512字节
     */
    private Long LOG_BUFFER_LIMIT = 512L;

    /**
     * 最后一次同步ID
     */
    public volatile long lastSyncId = 0;

    /**
     * 写日志
     */
    private EditLogBuffer currentBuffer;

    /**
     * 同步刷盘
     */
    private EditLogBuffer syncBuffer;

    public DoubleBuffer() {
    }

    public DoubleBuffer(EditLogBuffer currentBuffer, EditLogBuffer syncBuffer) {
        this.currentBuffer = currentBuffer;
        this.syncBuffer = syncBuffer;
    }

    public void write(EditLog editLog) throws IOException {
        currentBuffer.write(editLog);
    }

    public boolean shouldSyncToDisk(){
        if(currentBuffer.size() >= LOG_BUFFER_LIMIT) {
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
            EditLogBuffer temp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = temp;
        }
    }

    public void flush() throws IOException {
        syncBuffer.flush();
        syncBuffer.clear();
    }

    public EditLogBuffer getCurrentBuffer() {
        return currentBuffer;
    }

    public void setCurrentBuffer(EditLogBuffer currentBuffer) {
        this.currentBuffer = currentBuffer;
    }

    public EditLogBuffer getSyncBuffer() {
        return syncBuffer;
    }

    public void setSyncBuffer(EditLogBuffer syncBuffer) {
        this.syncBuffer = syncBuffer;
    }
}

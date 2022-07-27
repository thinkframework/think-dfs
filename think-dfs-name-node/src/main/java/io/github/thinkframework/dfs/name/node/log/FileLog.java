package io.github.thinkframework.dfs.name.node.log;

public class FileLog {

    private long txId;

    private String content;

    public FileLog(long txId, String content) {
        this.txId = txId;
        this.content = content;
    }

    public long getTxId() {
        return txId;
    }

    public void setTxId(long txId) {
        this.txId = txId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package io.github.thinkframework.dfs.commons.domain;

public class EditLog {

    private long id;

    private String content;

    public EditLog(String content) {
        this.content = content;
    }

    public EditLog(long txId, String content) {
        this.id = txId;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EditLog{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}

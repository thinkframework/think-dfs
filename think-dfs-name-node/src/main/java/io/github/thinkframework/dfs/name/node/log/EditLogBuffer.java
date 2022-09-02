package io.github.thinkframework.dfs.name.node.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thinkframework.dfs.commons.domain.EditLog;
import io.github.thinkframework.dfs.name.node.management.EditLogsService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EditLogBuffer {

    /**
     * 以行为单位,不需要格式化json
     */
    private Gson gson = new GsonBuilder()
            .create();

    private DoubleBuffer doubleBuffer;

    private ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();

    /**
     * 同步ID
     */
    private volatile long syncId = 0;


    public EditLogBuffer(DoubleBuffer doubleBuffer) {
        this.doubleBuffer = doubleBuffer;
    }

    /**
     * 写日志
     */
    public void write(EditLog editLog) throws IOException {
        bufferedOutputStream.write(gson.toJson(editLog).getBytes(StandardCharsets.UTF_8));
        bufferedOutputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 刷盘
     */
    public void flush() throws IOException {
        syncId = EditLogsService.threadLocal.get();
        Path path = Paths.get(System.getProperty("user.home") + File.separator + "tmp" + File.separator + "nameNode" + File.separator + "editLogs" + File.separator
                + doubleBuffer.lastSyncId + "_" + syncId
                + ".txt");
        doubleBuffer.lastSyncId = ++syncId; // 刷新最后一次同步id
        Files.deleteIfExists(path);
        path = Files.createFile(path);

        bufferedOutputStream.flush();
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            outputStream.getChannel()
                    .write(ByteBuffer.wrap(bufferedOutputStream.toByteArray()));
            outputStream.getChannel().force(true);
        } finally {
        }
    }

    public void clear() {
        bufferedOutputStream.reset();
    }

    /**
     * 缓冲区大小
     *
     * @return
     */
    public long size() {
        return bufferedOutputStream.size();
    }
}

package io.github.thinkframework.dfs.name.node.management;

import io.github.thinkframework.dfs.commons.domain.EditLog;
import io.github.thinkframework.dfs.commons.management.NameNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 文件目录树
 */
public class FileSystemService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    private NameNodeService nameNodeService;

    private EditLogsService editLogsService;

    public void start() {
        // 同步loadId;
        editLogsService.setTxIdSeq(nameNodeService.getEditLogsId());
        editLogsService.setSyncTxId(nameNodeService.getEditLogsId());
    }

    public void mkdir(String path) throws IOException {
        editLogsService.write(new EditLog(path));
        nameNodeService.mkdir(path);
    }

    public NameNodeService getNameNodeService() {
        return nameNodeService;
    }

    public void setNameNodeService(NameNodeService nameNodeService) {
        this.nameNodeService = nameNodeService;
    }

    public EditLogsService getEditLogsService() {
        return editLogsService;
    }

    public void setEditLogsService(EditLogsService editLogsService) {
        this.editLogsService = editLogsService;
    }
}

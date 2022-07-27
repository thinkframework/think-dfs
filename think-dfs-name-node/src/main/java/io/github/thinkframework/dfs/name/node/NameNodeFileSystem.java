package io.github.thinkframework.dfs.name.node;

import io.github.thinkframework.dfs.name.node.manager.ThinkFileLogManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件目录树
 */
public class NameNodeFileSystem {
    private NameNode root = new NameNode("/");

    private ThinkFileLogManager fileLogManager;

    /**
     * 创建目录
     * @param path
     */
    public void mkdir(String path){
        List<String> names = Arrays.stream(path.substring(1).split(File.separator))
                .filter(name -> name != null && name.length() != 0)
                .collect(Collectors.toList());
        NameNode parent = root;
        for (String name: names){
            NameNode p = parent;
            parent = p.find(name)
                    .orElseGet(() -> {
                        // todo 加锁? cas?
                        NameNode nameNode = new NameNode(name,p);
                        return nameNode;
                    });
        }
        NameNode node = parent;
        fileLogManager.write(node);
    }

    public ThinkFileLogManager getFileLogManager() {
        return fileLogManager;
    }

    public void setFileLogManager(ThinkFileLogManager fileLogManager) {
        this.fileLogManager = fileLogManager;
    }
}

package io.github.thinkframework.dfs;

import java.io.File;

/**
 * 文件目录树
 */
public class NemeNodeFS {
    private Node root = new Node();
    public void mkdir(String path){
        String[] names = path.split(File.separator);
        Node parent = root;
        for (String name: names){
            Node p = parent;
            parent = parent.find(name)
                    .orElseGet(() -> {
                        // todo 加锁? cas?
                        Node node = new Node(name,p);
                        return node;
                    });
        }
    }

}

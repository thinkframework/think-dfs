package io.github.thinkframework.dfs.name.node;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件目录树
 * @author lixiaobin
 */
public class NameNode {
    private String name;

    private NameNode parent;
    private Set<NameNode> children = new HashSet<>();

    public NameNode(String name) {
        this.name = name;
    }

    public NameNode(String name, NameNode parent) {
        this.name = name;
        this.parent = parent;
        parent.children.add(this);
    }

    public NameNode(String name, NameNode parent , Set<NameNode> children) {
        this.name = name;
        this.parent = parent;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NameNode getParent() {
        return parent;
    }

    public Set<NameNode> getChildren() {
        return children;
    }

    public void setChildren(Set<NameNode> children) {
        this.children = children;
    }

    public Optional<NameNode> find(String name){
        return children.stream().filter(child -> child.name.equals(name))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameNode nameNode = (NameNode) o;
        return Objects.equals(name, nameNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
//        return recursion(this)
//                .stream()
//                .collect(Collectors.joining(File.separator));
        return name;
    }

    public List<String> recursion(NameNode node){
        List<String> list = new ArrayList();
        if(node.parent != null) {
            list.addAll(recursion(node.parent));
        }
        if(node.name != null) {
            list.add(node.name);
        }
        return list;
    }
}

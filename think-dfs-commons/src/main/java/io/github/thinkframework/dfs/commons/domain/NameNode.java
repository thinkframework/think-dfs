package io.github.thinkframework.dfs.commons.domain;

import java.util.*;

/**
 * 文件目录树
 * @author lixiaobin
 */
public class NameNode {

    private String name;

    private transient NameNode parent;

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

    public void addChild(NameNode child){
        child.parent = this;
        children.add(child);
    }

    public Optional<NameNode> find(String name){
        return children.stream().filter(child -> child.name.equals(name))
                .findFirst();
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

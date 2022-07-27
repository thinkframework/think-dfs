package io.github.thinkframework.dfs;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件目录树
 * @author lixiaobin
 */
public class Node {
    private String name;

    private Node parent;
    private Set<Node> children;

    public Node() {
    }

    public Node(String name,Node parent) {
        this.name = name;
        this.parent = parent;
        parent.children.add(this);
    }

    public Node(String name, Set<Node> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Node> getChildren() {
        return children;
    }

    public void setChildren(Set<Node> children) {
        this.children = children;
    }

    public Optional<Node> find(String name){
        return children.stream().filter(child -> child.name.equals(name))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return r(this)
                .stream()
                .collect(Collectors.joining(File.separator));
    }

    public List<String> r(Node parent){
        List<String> list = new ArrayList();
        if(parent != null) {
            list.addAll(r(parent));
        }
        if(name != null) {
            list.add(name);
        }
        return list;
    }
}

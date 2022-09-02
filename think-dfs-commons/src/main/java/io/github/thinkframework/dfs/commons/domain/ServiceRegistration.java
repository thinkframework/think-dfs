package io.github.thinkframework.dfs.commons.domain;

import io.github.thinkframework.dfs.commons.discovery.ServiceInstance;

public class ServiceRegistration implements ServiceInstance {

    private String name;

    private String host;

    private int port;

    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

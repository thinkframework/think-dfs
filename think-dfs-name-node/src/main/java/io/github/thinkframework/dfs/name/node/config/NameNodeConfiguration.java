package io.github.thinkframework.dfs.name.node.config;

import io.github.thinkframework.dfs.commons.config.ApplicationConfiguration;

public class NameNodeConfiguration implements ApplicationConfiguration {
    @Override
    public String getWorkdir() {
        return "nameNode";
    }
}

package io.github.thinkframework.dfs.backup.node.config;

import io.github.thinkframework.dfs.commons.config.ApplicationConfiguration;

public class BackupNodeConfiguration implements ApplicationConfiguration {
    @Override
    public String getWorkdir() {
        return "backupNode";
    }
}

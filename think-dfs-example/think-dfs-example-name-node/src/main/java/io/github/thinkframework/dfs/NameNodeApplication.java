package io.github.thinkframework.dfs;

import io.github.thinkframework.dfs.commons.management.NameNodeService;
import io.github.thinkframework.dfs.name.node.config.NameNodeConfiguration;
import io.github.thinkframework.dfs.name.node.log.DoubleBuffer;
import io.github.thinkframework.dfs.name.node.log.EditLogBuffer;
import io.github.thinkframework.dfs.name.node.management.EditLogsService;
import io.github.thinkframework.dfs.name.node.management.FileSystemService;
import io.github.thinkframework.dfs.name.node.server.FSImageSocketChannelServer;
import io.github.thinkframework.dfs.name.node.server.NameNodeServer;
import io.github.thinkframework.dfs.name.node.web.rest.EditLogsResource;
import io.github.thinkframework.dfs.name.node.web.rest.FileSystemResource;
import io.github.thinkframework.dfs.name.node.web.rest.ServiceRegistryResources;

import java.io.IOException;

/**
 *
 * @author lixiaobin
 */
public class NameNodeApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 异步缓存
        DoubleBuffer doubleBuffer = new DoubleBuffer();
        doubleBuffer.setCurrentBuffer(new EditLogBuffer(doubleBuffer));
        doubleBuffer.setSyncBuffer(new EditLogBuffer(doubleBuffer));

        // 节点
        NameNodeService nameNodeService = new NameNodeService();
        nameNodeService.setApplicationConfiguration(new NameNodeConfiguration());
        nameNodeService.start(); // 多线程join或者不使用多线程,必须加载完再继续
        // 日志
        EditLogsService editLogsService = new EditLogsService();
        editLogsService.setBuffer(doubleBuffer);


        // 注册表
        ServiceRegistryResources serviceRegistryResources = new ServiceRegistryResources();


        EditLogsResource editLogsResource = new EditLogsResource();
        editLogsResource.setEditLogsService(editLogsService);

        // 文件系统
        FileSystemService fileSystemService =new FileSystemService();
        fileSystemService.setNameNodeService(nameNodeService);
        fileSystemService.setEditLogsService(editLogsService);
        fileSystemService.start(); // 同步nameNodeService和editLogsService的id

        FileSystemResource fileSystemResource = new FileSystemResource();
        fileSystemResource.setNameNodeFileSystem(fileSystemService);
        //
        NameNodeServer nameNodeServer = new NameNodeServer();
        nameNodeServer.setNameNodeServiceRegistry(serviceRegistryResources);
        nameNodeServer.setFileLogsResources(editLogsResource);
        nameNodeServer.setNameNodeFileSystemService(fileSystemResource);

        // Fsimage同步
        FSImageSocketChannelServer fsImageSocketChannelServer = new FSImageSocketChannelServer();
        fsImageSocketChannelServer.start(); // 启动一个线程
        // 启动
        nameNodeServer.start();
        nameNodeServer.awaitTermination();
    }

}

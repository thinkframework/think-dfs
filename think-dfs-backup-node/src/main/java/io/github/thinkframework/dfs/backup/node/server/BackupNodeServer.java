package io.github.thinkframework.dfs.backup.node.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thinkframework.dfs.backup.node.config.BackupNodeConfiguration;
import io.github.thinkframework.dfs.backup.node.service.FSImageService;
import io.github.thinkframework.dfs.commons.config.ApplicationConfiguration;
import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.management.NameNodeService;
import io.github.thinkframework.dfs.rpc.EditLogsResourceGrpc;
import io.github.thinkframework.dfs.rpc.Logs;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackupNodeServer {

    private static final Logger logger = LoggerFactory.getLogger(BackupNodeServer.class);

    private ApplicationConfiguration applicationConfiguration;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private EditLogsResourceGrpc.EditLogsResourceBlockingStub stub;

    private volatile boolean shutdown;

    private NameNodeService nameNodeService;

    private Timer fetchTimer;

    private Thread thread;


    public BackupNodeServer() {
        stub = EditLogsResourceGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost", Constants.NAME_NODE_PORT)
                        .usePlaintext()
                        .build());
    }

    public void start() throws IOException {
        fetchTimer = new Timer("文件获取", true);

        fetchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long id = Long.valueOf(nameNodeService.getEditLogsId());
                if (id != 0L) {
                    id ++;
                }
                logger.debug("文件id: {}", id);
                String response = stub.get(Logs.newBuilder()
                                .setId(id)
                                .build())
                        .getBody();
                BufferedReader bufferedReader = new BufferedReader(new StringReader(response));
                try {
                    for (String line = bufferedReader.readLine();
                         line != null && line.length() > 0;
                         line = bufferedReader.readLine()) {
                        nameNodeService.mkdir(line);
                    }
                } catch (IOException e) {
                    logger.error("文件获取", e);
                }
            }
        }, 1000L, 1000L);

        thread = Thread.currentThread();
    }


    public void awaitTermination() {
        while (!shutdown) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {

            }
        }
    }

    public void shutdown() {
        shutdown = true;
        thread.interrupt();
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public NameNodeService getNameNodeService() {
        return nameNodeService;
    }

    public void setNameNodeService(NameNodeService nameNodeService) {
        this.nameNodeService = nameNodeService;
    }
}

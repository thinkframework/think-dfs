package io.github.thinkframework.dfs.name.node.web.rest;


import com.google.protobuf.ByteString;
import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.name.node.management.EditLogsService;
import io.github.thinkframework.dfs.rpc.EditLogsResourceGrpc;
import io.github.thinkframework.dfs.rpc.Logs;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 操作日志
 */
public class EditLogsResource extends EditLogsResourceGrpc.EditLogsResourceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(EditLogsResource.class);

    Path path = Paths.get(System.getProperty("user.home")+ File.separator
            + "tmp" + File.separator
            + "nameNode" + File.separator
            +"editLogs");

    private EditLogsService editLogsService;

    @Override
    public void get(Logs request, StreamObserver<Logs> responseObserver) {
        Stream<Path> stream = null;
        try {
            stream = Files.list(path)
                    .filter(p -> p.getFileName().toString().startsWith(String.valueOf(request.getId())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Optional<Path> optionalPath;
        if((optionalPath = stream.findFirst()).isPresent()) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(optionalPath.get().toFile(), "rw")){;
                ByteBuffer byteBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE / 2);
                ByteBuffer fileBuffer = ByteBuffer.allocate(Constants.BUFFER_SIZE * 2);

                FileChannel fileChannel = randomAccessFile.getChannel();
                for (int count;(count = fileChannel.read(byteBuffer)) > 0;byteBuffer.clear()) {
                    byteBuffer.flip();
                    fileBuffer.put(byteBuffer);
                }
                fileBuffer.flip();
                Logs response = request.toBuilder()
                        .setBodyBytes(ByteString.copyFrom(fileBuffer, fileBuffer.limit()))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public EditLogsService getEditLogsService() {
        return editLogsService;
    }

    public void setEditLogsService(EditLogsService editLogsService) {
        this.editLogsService = editLogsService;
    }
}

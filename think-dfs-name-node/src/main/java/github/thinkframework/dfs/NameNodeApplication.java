package github.thinkframework.dfs;

import github.thinkframework.dfs.ThinkFileSystem;
import github.thinkframework.dfs.ThinkServiceRegistry;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.ServerSocket;

public class NameNodeApplication {

    public static void main(String[] args) throws IOException {

        ServerBuilder.forPort(9001)
                .addService(new ThinkServiceRegistry())
                .addService(new ThinkFileSystem())
                .build()
                .start();
        while (true){
            Thread.onSpinWait();
        }
    }

}

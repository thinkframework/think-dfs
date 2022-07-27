package github.thinkframework.dfs;

import io.github.thinkframework.dfs.ThinkServiceRegistry;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class DateNodeApplication {
    public static void main(String[] args) throws IOException {

        ServerBuilder.forPort(9001)
                .addService(new ThinkServiceRegistry())
                .build()
                .start();
        while (true){
            Thread.onSpinWait();
        }
    }
}

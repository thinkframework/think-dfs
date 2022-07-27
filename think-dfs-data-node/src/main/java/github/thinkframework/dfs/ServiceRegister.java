package github.thinkframework.dfs;

import io.github.thinkframework.dfs.rpc.Registration;
import io.github.thinkframework.dfs.rpc.ThinkServiceRegistryGrpc;
import io.grpc.ManagedChannelBuilder;

public class ServiceRegister {

    ThinkServiceRegistryGrpc.ThinkServiceRegistryBlockingStub stub;

    public ServiceRegister() {
        stub = ThinkServiceRegistryGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost",9001)
                        .usePlaintext()
                        .build());
    }

    public void register(Registration registration){
        stub.register(registration);
    }
}

package github.thinkframework.dfs;

import io.grpc.ManagedChannelBuilder;
import io.thinkframework.dfs.rpc.Registration;
import io.thinkframework.dfs.rpc.ThinkServiceRegistryGrpc;

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

package github.thinkframework.dfs.data.node.management;

import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.rpc.NameNodeServiceRegistryGrpc;
import io.github.thinkframework.dfs.rpc.Registration;
import io.grpc.ManagedChannelBuilder;

public class ServiceRegister {

    NameNodeServiceRegistryGrpc.NameNodeServiceRegistryBlockingStub stub;

    public ServiceRegister() {
        stub = NameNodeServiceRegistryGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress("localhost", Constants.NAME_NODE_PORT)
                        .usePlaintext()
                        .build());
    }

    public void register(Registration registration){
        stub.register(registration);
    }
}

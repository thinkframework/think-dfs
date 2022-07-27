package io.github.thinkframework.dfs;


import io.github.thinkframework.dfs.rpc.Registration;
import io.github.thinkframework.dfs.rpc.ThinkServiceRegistryGrpc;
import io.grpc.stub.StreamObserver;

public class ThinkServiceRegistry extends ThinkServiceRegistryGrpc.ThinkServiceRegistryImplBase {
    @Override
    public void register(Registration request, StreamObserver<Registration> responseObserver) {
        super.register(request, responseObserver);
    }

    @Override
    public void deregister(Registration request, StreamObserver<Registration> responseObserver) {
        super.deregister(request, responseObserver);
    }

    @Override
    public void setStatus(Registration request, StreamObserver<Registration> responseObserver) {
        super.setStatus(request, responseObserver);
    }
}

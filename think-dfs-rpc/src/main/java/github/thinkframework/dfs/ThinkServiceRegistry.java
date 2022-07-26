package github.thinkframework.dfs;

import io.grpc.stub.StreamObserver;
import io.thinkframework.dfs.rpc.Registration;
import io.thinkframework.dfs.rpc.ThinkServiceRegistryGrpc;

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

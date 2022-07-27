package io.github.thinkframework.dfs.name.node.service;


import io.github.thinkframework.dfs.rpc.Registration;
import io.github.thinkframework.dfs.rpc.ThinkServiceRegistryGrpc;
import io.grpc.stub.StreamObserver;

public class NameNodeServiceRegistry extends ThinkServiceRegistryGrpc.ThinkServiceRegistryImplBase {
    @Override
    public void register(Registration request, StreamObserver<Registration> responseObserver) {
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deregister(Registration request, StreamObserver<Registration> responseObserver) {
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    @Override
    public void setStatus(Registration request, StreamObserver<Registration> responseObserver) {
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }
}

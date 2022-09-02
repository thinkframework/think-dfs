package io.github.thinkframework.dfs.name.node.web.rest;


import io.github.thinkframework.dfs.commons.domain.ServiceRegistration;
import io.github.thinkframework.dfs.name.node.service.ServiceRegistryService;
import io.github.thinkframework.dfs.rpc.Registration;
import io.github.thinkframework.dfs.rpc.NameNodeServiceRegistryGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务注册表
 *
 */
public class ServiceRegistryResources extends NameNodeServiceRegistryGrpc.NameNodeServiceRegistryImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryResources.class);

    private ServiceRegistryService serviceRegistryService;

    /**
     * 注册
     * @param request
     * @param responseObserver
     */
    @Override
    public void register(Registration request, StreamObserver<Registration> responseObserver) {
        logger.debug("register : {}", request);
        ServiceRegistration registration = new ServiceRegistration();
        registration.setName(request.getName());
        serviceRegistryService.register(registration);
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    /**
     * 注销
     * @param request
     * @param responseObserver
     */
    @Override
    public void deregister(Registration request, StreamObserver<Registration> responseObserver) {
        logger.debug("deregister : {}", request);
        ServiceRegistration registration = new ServiceRegistration();
        registration.setName(request.getName());
        serviceRegistryService.deregister(registration);
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    /**
     * 状态更新
     * @param request
     * @param responseObserver
     */
    @Override
    public void setStatus(Registration request, StreamObserver<Registration> responseObserver) {
        logger.debug("setStatus : {}", request);
        ServiceRegistration registration = new ServiceRegistration();
        registration.setName(request.getName());
        serviceRegistryService.setStatus(registration);
        Registration reponse = request;
        responseObserver.onNext(reponse);
        responseObserver.onCompleted();
    }

    public ServiceRegistryService getServiceRegistryService() {
        return serviceRegistryService;
    }

    public void setServiceRegistryService(ServiceRegistryService serviceRegistryService) {
        this.serviceRegistryService = serviceRegistryService;
    }
}

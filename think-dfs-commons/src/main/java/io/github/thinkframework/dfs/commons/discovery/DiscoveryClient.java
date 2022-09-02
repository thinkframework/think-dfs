package io.github.thinkframework.dfs.commons.discovery;

import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.domain.ServiceRegistration;

import java.util.List;

public class DiscoveryClient {

    public List<String> getServices(){
        return List.of("dataNode");
    }

    public List<ServiceInstance> getInstances(String serviceId){
        ServiceRegistration serviceRegistration = new ServiceRegistration();
        serviceRegistration.setHost("localhost");
        serviceRegistration.setPort(Constants.DATA_NODE_PORT);
        return List.of(serviceRegistration);
    }
}

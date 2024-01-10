package io.github.thinkframework.dfs.commons.discovery;

import io.github.thinkframework.dfs.commons.config.Constants;
import io.github.thinkframework.dfs.commons.domain.ServiceRegistration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiscoveryClient {

    public List<String> getServices(){
        return Stream.of("dataNode").collect(Collectors.toList());
    }

    public List<ServiceInstance> getInstances(String serviceId){
        ServiceRegistration serviceRegistration = new ServiceRegistration();
        serviceRegistration.setHost("localhost");
        serviceRegistration.setPort(Constants.DATA_NODE_PORT);
        return Stream.of(serviceRegistration).collect(Collectors.toList());
    }
}

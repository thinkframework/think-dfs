package io.github.thinkframework.dfs.name.node.service;

import io.github.thinkframework.dfs.commons.domain.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryService.class);

    private Map<String, List<ServiceRegistration>> registry = new ConcurrentHashMap<>();

    /**
     * 注册
     * @param registration
     */
    public void register(ServiceRegistration registration) {
        List<ServiceRegistration> list = registry.putIfAbsent(registration.getName(),new CopyOnWriteArrayList<>());
        if(list.stream()
                .noneMatch(value -> value.getName().equals(registration.getName()))){
            list.add(registration);
        }
    }

    /**
     * 注销
     * @param registration
     */
    public void deregister(ServiceRegistration registration) {
        List<ServiceRegistration> list = registry.putIfAbsent(registration.getName(),new CopyOnWriteArrayList<>());
        Optional<ServiceRegistration> optional = list.stream()
                .filter(value -> value.getName().equals(registration.getName()))
                .findFirst();
        if (optional.isPresent()){
            list.remove(optional.get());
        }
    }

    /**
     * 状态更新
     * @param registration
     */
    public void setStatus(ServiceRegistration registration) {
        List<ServiceRegistration> list = registry.putIfAbsent(registration.getName(),new CopyOnWriteArrayList<>());
        Optional<ServiceRegistration> optional = list.stream()
                .filter(value -> value.getName().equals(registration.getName()))
                .findFirst();
        optional.ifPresent(value -> value.setStatus(registration.getStatus()));
    }
}

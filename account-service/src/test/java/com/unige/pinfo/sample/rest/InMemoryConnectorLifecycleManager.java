package com.unige.pinfo.sample.rest;


import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

public class InMemoryConnectorLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props1 = InMemoryConnector.switchIncomingChannelsToInMemory("user-update");   
        Map<String, String> props2 = InMemoryConnector.switchIncomingChannelsToInMemory("org-update");  
        env.putAll(props1);
        env.putAll(props2);
        return env;  
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();  
    }
}
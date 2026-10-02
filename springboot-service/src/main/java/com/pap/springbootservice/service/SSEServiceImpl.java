package com.pap.springbootservice.service;

import org.springframework.stereotype.Service;

import com.pap.springbootservice.model.SSEEventCache;
import com.pap.springbootservice.model.SSEEvent;

@Service 
public class SSEServiceImpl implements SSEService {
    private final SSEEventCache cache;

    public SSEServiceImpl(SSEEventCache sseCacheRegistry) {
        this.cache = sseCacheRegistry;
    }

    @Override
    public void sendEvent(SSEEvent event) {
        cache.addEvent(event);
    }

    @Override
    public SSEEvent getEvent() {
        return cache.getEvent();
    }

    @Override
    public boolean isEventAvailable() {
        return cache.hasEvent();
    }

}

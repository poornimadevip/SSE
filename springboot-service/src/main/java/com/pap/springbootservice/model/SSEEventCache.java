package com.pap.springbootservice.model;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

@Component 
public class SSEEventCache {

    private ConcurrentLinkedQueue<SSEEvent> eventCache;
    
    public SSEEventCache() {
        this.eventCache = new ConcurrentLinkedQueue<>();
    }

    public void addEvent(SSEEvent event) {
        eventCache.add(event);
    }

    public SSEEvent getEvent() {
        if (eventCache.isEmpty()) {
            return null;
        }
        return eventCache.poll();
    }

    public boolean hasEvent() {
        return !eventCache.isEmpty();
    }
    
}

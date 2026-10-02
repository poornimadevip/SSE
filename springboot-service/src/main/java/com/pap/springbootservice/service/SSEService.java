package com.pap.springbootservice.service;

import com.pap.springbootservice.model.SSEEvent;

public interface SSEService {
    void sendEvent(SSEEvent event);
    boolean isEventAvailable();
    SSEEvent getEvent();
}
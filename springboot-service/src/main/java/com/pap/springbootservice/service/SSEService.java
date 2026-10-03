package com.pap.springbootservice.service;

import java.util.function.Consumer;

import com.pap.springbootservice.model.SSEEvent;

public interface SSEService {
    void sendEvent(SSEEvent event);
    void addEventListener(Consumer<SSEEvent> listener);
    void removeEventListener(Consumer<SSEEvent> listener);
}
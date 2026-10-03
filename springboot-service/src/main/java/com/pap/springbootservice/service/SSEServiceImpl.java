package com.pap.springbootservice.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.pap.springbootservice.model.SSEEvent;

@Service
public class SSEServiceImpl implements SSEService {
    private final CopyOnWriteArrayList<Consumer<SSEEvent>> eventListeners = new CopyOnWriteArrayList<>();
    private final Deque<SSEEvent> pendingEvents = new ArrayDeque<>();

    @Override
    public synchronized void sendEvent(SSEEvent event) {
        if (eventListeners.isEmpty()) {
            pendingEvents.addLast(event);
            return;
        }

        eventListeners.forEach(listener -> listener.accept(event));
    }

    @Override
    public synchronized void addEventListener(Consumer<SSEEvent> listener) {
        eventListeners.add(listener);
        while (!pendingEvents.isEmpty() && eventListeners.contains(listener)) {
            listener.accept(pendingEvents.removeFirst());
        }
    }

    @Override
    public synchronized void removeEventListener(Consumer<SSEEvent> listener) {
        eventListeners.remove(listener);
    }
}

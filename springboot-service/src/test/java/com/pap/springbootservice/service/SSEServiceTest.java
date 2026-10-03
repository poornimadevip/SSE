package com.pap.springbootservice.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.pap.springbootservice.model.SSEEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SSEServiceTest {

    @Test
    void publishesNewEventsToRegisteredListeners() {
        SSEServiceImpl service = new SSEServiceImpl();
        AtomicReference<SSEEvent> received = new AtomicReference<>();
        service.addEventListener(received::set);
        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "A new event");

        service.sendEvent(event);

        assertEquals(event, received.get());
    }

    @Test
    void deliversPendingEventsWhenAListenerConnects() {
        SSEServiceImpl service = new SSEServiceImpl();
        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "A pending event");
        service.sendEvent(event);
        AtomicReference<SSEEvent> received = new AtomicReference<>();

        service.addEventListener(received::set);

        assertEquals(event, received.get());
    }

    @Test
    void doesNotPublishEventsToRemovedListeners() {
        SSEServiceImpl service = new SSEServiceImpl();
        AtomicReference<SSEEvent> received = new AtomicReference<>();
        java.util.function.Consumer<SSEEvent> listener = received::set;
        service.addEventListener(listener);
        service.removeEventListener(listener);

        service.sendEvent(new SSEEvent("1", LocalDateTime.now(), "A new event"));

        assertNull(received.get());
    }
}

package com.pap.springbootservice.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pap.springbootservice.model.SSEEvent;
import com.pap.springbootservice.service.SSEService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SSEControllerTest {

    @Mock
    private SSEService sseService;

    @Mock
    private SseEmitter emitter;

    @Captor
    private ArgumentCaptor<Consumer<SSEEvent>> listenerCaptor;

    @InjectMocks
    private SSEController sseController;

    @Test
    void keepsStreamOpenAndSendsNewEvents() throws IOException {
        SSEController controller = new SSEController(sseService) {
            @Override
            protected SseEmitter createEmitter() {
                return emitter;
            }
        };

        assertNotNull(controller.getEvents());

        verify(sseService).addEventListener(listenerCaptor.capture());

        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "A new event");
        listenerCaptor.getValue().accept(event);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
        verify(sseService, never()).removeEventListener(listenerCaptor.getValue());
    }

    @Test
    void removesListenerWhenSendingFails() throws IOException {
        doThrow(new IOException("client disconnected"))
            .when(emitter).send(any(SseEmitter.SseEventBuilder.class));
        SSEController controller = new SSEController(sseService) {
            @Override
            protected SseEmitter createEmitter() {
                return emitter;
            }
        };
        controller.getEvents();

        verify(sseService).addEventListener(listenerCaptor.capture());
        listenerCaptor.getValue().accept(new SSEEvent("1", LocalDateTime.now(), "A new event"));

        verify(sseService).removeEventListener(listenerCaptor.getValue());
        verify(emitter).completeWithError(any(IOException.class));
    }

    @Test
    void sendEventsPublishesValidEvent() {
        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "A new event");

        ResponseEntity<String> response = sseController.sendEvents(event);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sseService).sendEvent(event);
    }

    @Test
    void sendEventsRejectsNullEvent() {
        ResponseEntity<String> response = sseController.sendEvents(null);

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(sseService, never()).sendEvent(any());
    }

    @Test
    void sendEventsRejectsEventWithoutId() {
        ResponseEntity<String> response = sseController.sendEvents(
            new SSEEvent(null, LocalDateTime.now(), "A new event")
        );

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(sseService, never()).sendEvent(any());
    }
}

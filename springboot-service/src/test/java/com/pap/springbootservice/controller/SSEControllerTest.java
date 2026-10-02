package com.pap.springbootservice.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pap.springbootservice.model.SSEEvent;
import com.pap.springbootservice.service.SSEService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SSEControllerTest {

    @Mock
    private SSEService sseService;

    @InjectMocks
    private SSEController sseController;

    @Test
    void streamsEventsUntilTheQueueIsEmpty() {
        when(sseService.isEventAvailable()).thenReturn(true).thenReturn(false);

        when(sseService.getEvent())
            .thenReturn(
                new SSEEvent("1", LocalDateTime.now(), "First event"),
                new SSEEvent("2", LocalDateTime.now(), "Second event"),
                null
            );

        SseEmitter emitter = sseController.getEvents();

        assertNotNull(emitter);
        verify(sseService, times(1)).getEvent();
    }

    @Test 
    void streamEventsIfQueueIsEmpty(){
        when(sseService.isEventAvailable()).thenReturn(false);

        SseEmitter emitter = sseController.getEvents();
        assertNotNull(emitter);
        verify(sseService, never()).getEvent();
    }

    @Test 
    void streamEventsIfEventNull(){
        when(sseService.isEventAvailable()).thenReturn(true).thenReturn(false);
        when(sseService.getEvent()).thenReturn(null);
        SseEmitter emitter = sseController.getEvents();
        assertNotNull(emitter);
        verify(sseService, times(1)).getEvent();
        verify(sseService, times(2)).isEventAvailable();
    }

    @Test 
    void streamEventsWhenException(){
        SseEmitter emitter = mock(SseEmitter.class);
        RuntimeException error = new RuntimeException("test");
        when(sseService.getEvent()).thenThrow(error);
        when(sseService.isEventAvailable()).thenReturn(true);

        SSEController controller = new SSEController(sseService){
            @Override 
            protected SseEmitter createEmitter() {
                return emitter;
            }
        };

        SseEmitter actualemitter = controller.getEvents();
        verify(actualemitter).completeWithError(error);
    }
}

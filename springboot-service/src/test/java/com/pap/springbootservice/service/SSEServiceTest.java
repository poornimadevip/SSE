package com.pap.springbootservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pap.springbootservice.model.SSEEvent;
import com.pap.springbootservice.model.SSEEventCache;

@ExtendWith(MockitoExtension.class)
public class SSEServiceTest {

    @Mock 
    private SSEEventCache cache;

    @InjectMocks 
    private SSEServiceImpl sseService;

    @Test 
    public void testSendEvent() {
        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "Test Event");
        doNothing().when(cache).addEvent(event);
        sseService.sendEvent(event);
        verify(cache, times(1)).addEvent(event);
    }

    @Test 
    public void testGetEvent(){
        SSEEvent event = new SSEEvent("1", LocalDateTime.now(), "Test Event");
        when(cache.getEvent()).thenReturn(event);
        SSEEvent result = sseService.getEvent();
        verify(cache, times(1)).getEvent();
        assertEquals(event.message(), result.message());
    }

    @Test 
    public void testIsEventAvailableTrue(){
        when(cache.hasEvent()).thenReturn(true);
        assertEquals(true, sseService.isEventAvailable());
    }

    @Test 
    public void testIsEventAvailableFalse(){
        when(cache.hasEvent()).thenReturn(false);
        assertEquals(false, sseService.isEventAvailable());
    }
    
}

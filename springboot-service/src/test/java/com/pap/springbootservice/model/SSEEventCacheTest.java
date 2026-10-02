package com.pap.springbootservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class SSEEventCacheTest {
    
    @Test 
    void testAddEvent(){
        SSEEventCache cache = new SSEEventCache();
        cache.addEvent(new SSEEvent("1", LocalDateTime.now(), "message1"));
        
        assertTrue(cache.hasEvent());
    }

    @Test 
    void testGetEvent(){
        SSEEventCache cache = new SSEEventCache();
        cache.addEvent(new SSEEvent("1", LocalDateTime.now(), "message1"));
        cache.addEvent(new SSEEvent("2", LocalDateTime.now(), "message2"));

        SSEEvent event = cache.getEvent();
        assertEquals("message1", event.message());
    }

    @Test 
    void testGetEventWhenCacheEmpty(){
        SSEEventCache cache = new SSEEventCache();

        assertNull(cache.getEvent());
    }

    @Test 
    void testHasEventWhenCacheEmpty(){
        SSEEventCache cache = new SSEEventCache();
        assertFalse(cache.hasEvent());
    }
}

package com.pap.springbootservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pap.springbootservice.model.SSEEvent;
import com.pap.springbootservice.service.SSEService;

@RestController 
@RequestMapping("/v1/events") 
public class SSEController {
    private final SSEService sseService;

    public SSEController(SSEService sseService) {
        this.sseService = sseService;
    }

    protected SseEmitter createEmitter(){
        return new SseEmitter();
    }

    @GetMapping("/stream")
    public SseEmitter getEvents() {
        SseEmitter emitter = createEmitter();
        try {
            while (sseService.isEventAvailable()) {
                SSEEvent event = sseService.getEvent();
                if (event != null) {
                    emitter.send(event);
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

}

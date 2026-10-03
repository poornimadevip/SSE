package com.pap.springbootservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pap.springbootservice.model.SSEEvent;
import com.pap.springbootservice.service.SSEService;

@Slf4j
@RestController
@RequestMapping("/v1/events")
public class SSEController {
    private final SSEService sseService;

    public SSEController(SSEService sseService) {
        this.sseService = sseService;
    }

    protected SseEmitter createEmitter() {
        return new SseEmitter();
    }

    @GetMapping("/stream")
    public SseEmitter getEvents() {
        SseEmitter emitter = createEmitter();
        log.info("Starting SSE event stream");
        try {
            while (sseService.isEventAvailable()) {
                log.info("SSE Event available");
                SSEEvent event = sseService.getEvent();
                if (event != null) {
                    emitter.send(event);
                    log.debug("Sent SSE event with ID {}", event.id());
                }
                Thread.sleep(1000);
            }
            log.info("SSE event stream completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("SSE event stream was interrupted", e);
            emitter.completeWithError(e);
        } catch (Exception e) {
            log.error("Error while streaming SSE events", e);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @PostMapping
    public ResponseEntity<String> sendEvents(@RequestBody SSEEvent event) {
        if (event == null || event.id() == null) {
            log.warn("Rejecting invalid SSE event: event or event ID is null");
            return ResponseEntity.badRequest().body("invalid event");
        }

        sseService.sendEvent(event);
        log.info("Accepted SSE event with ID {}", event.id());

        return ResponseEntity.created(null).build();
    }

}

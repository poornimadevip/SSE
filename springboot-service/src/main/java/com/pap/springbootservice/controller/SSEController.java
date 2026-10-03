package com.pap.springbootservice.controller;

import java.io.IOException;
import java.util.function.Consumer;

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
        return new SseEmitter(0L);
    }

    @GetMapping("/stream")
    public SseEmitter getEvents() {
        SseEmitter emitter = createEmitter();
        Consumer<SSEEvent> listener = new EmitterEventListener(emitter);
        sseService.addEventListener(listener);
        emitter.onCompletion(() -> sseService.removeEventListener(listener));
        emitter.onTimeout(() -> sseService.removeEventListener(listener));
        emitter.onError(error -> {
            log.warn("SSE client connection failed", error);
            sseService.removeEventListener(listener);
        });
        log.info("Starting SSE event stream");
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

    private final class EmitterEventListener implements Consumer<SSEEvent> {
        private final SseEmitter emitter;

        private EmitterEventListener(SseEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void accept(SSEEvent event) {
            try {
                emitter.send(SseEmitter.event()
                    .name("sse-event")
                    .data(event));
                log.debug("Sent SSE event with ID {}", event.id());
            } catch (IOException e) {
                log.warn("Unable to send SSE event; closing the client stream", e);
                sseService.removeEventListener(this);
                emitter.completeWithError(e);
            }
        }
    }

}

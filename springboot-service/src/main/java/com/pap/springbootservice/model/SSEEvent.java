package com.pap.springbootservice.model;

import java.time.LocalDateTime;

public record SSEEvent(String id, LocalDateTime timestamp, String message) {
}

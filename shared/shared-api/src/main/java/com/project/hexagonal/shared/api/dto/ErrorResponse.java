package com.project.hexagonal.shared.api.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        String details,
        LocalDateTime timestamp
) {
}

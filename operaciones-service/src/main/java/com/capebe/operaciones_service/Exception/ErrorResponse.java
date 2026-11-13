package com.capebe.operaciones_service.Exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String mensaje, LocalDateTime fecha) {
}
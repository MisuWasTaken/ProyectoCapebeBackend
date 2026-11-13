package com.capebe.operaciones_service.Exception;

import java.time.OffsetDateTime;

public record ErrorResponse(int status, String mensaje, OffsetDateTime fecha) {
}


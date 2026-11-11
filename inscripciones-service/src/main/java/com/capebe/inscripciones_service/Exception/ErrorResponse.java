package com.capebe.inscripciones_service.Exception;

import java.time.OffsetDateTime;

public record ErrorResponse(int status, String mensaje, OffsetDateTime fecha) {
}

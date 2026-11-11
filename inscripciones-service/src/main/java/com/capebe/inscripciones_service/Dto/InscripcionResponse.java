package com.capebe.inscripciones_service.Dto;

import java.time.OffsetDateTime;

public record InscripcionResponse(
        Long id, Long usuarioId, Long vueloId, String nombre, String email,
        Boolean consentimientoAceptado, OffsetDateTime createdAt, OffsetDateTime updatedAt
) {
}

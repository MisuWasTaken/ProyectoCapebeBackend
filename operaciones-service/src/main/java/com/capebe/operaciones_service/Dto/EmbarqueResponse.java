package com.capebe.operaciones_service.Dto;

import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import java.time.OffsetDateTime;

public record EmbarqueResponse(
        Long id,
        Long vueloId,
        Long inscripcionId,
        EstadoEmbarque estado,
        String fotoUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
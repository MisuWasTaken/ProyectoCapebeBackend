package com.capebe.operaciones_service.Dto;

import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmbarqueResponse {
    private Long id;
    private Long vueloId;
    private Long inscripcionId;
    private EstadoEmbarque estado;
    private String fotoUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
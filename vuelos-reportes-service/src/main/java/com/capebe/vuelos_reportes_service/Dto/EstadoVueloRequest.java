package com.capebe.vuelos_reportes_service.Dto;

import com.capebe.vuelos_reportes_service.Entity.EstadoVuelo;
import jakarta.validation.constraints.NotNull;

public record EstadoVueloRequest(
        @NotNull
        EstadoVuelo estado
) {
}
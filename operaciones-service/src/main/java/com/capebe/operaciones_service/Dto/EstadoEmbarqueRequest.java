package com.capebe.operaciones_service.Dto;

import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import jakarta.validation.constraints.NotNull;

public record EstadoEmbarqueRequest(
        @NotNull(message = "El estado del embarque es obligatorio.")
        EstadoEmbarque estado
) {}
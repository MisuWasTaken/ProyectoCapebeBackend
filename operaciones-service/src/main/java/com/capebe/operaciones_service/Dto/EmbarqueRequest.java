package com.capebe.operaciones_service.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmbarqueRequest(
        @NotNull @Positive Long vueloId,
        @NotNull @Positive Long inscripcionId
) {
}

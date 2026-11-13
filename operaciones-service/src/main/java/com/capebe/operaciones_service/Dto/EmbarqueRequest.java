package com.capebe.operaciones_service.Dto;

import jakarta.validation.constraints.NotNull;

public record EmbarqueRequest(
        @NotNull(message = "El ID del vuelo es obligatorio.")
        Long vueloId,

        @NotNull(message = "El ID de la inscripción es obligatorio.")
        Long inscripcionId
) {}
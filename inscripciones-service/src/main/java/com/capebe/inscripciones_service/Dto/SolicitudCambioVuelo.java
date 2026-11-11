package com.capebe.inscripciones_service.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitudCambioVuelo(@NotNull @Positive Long nuevoVueloId) {
}

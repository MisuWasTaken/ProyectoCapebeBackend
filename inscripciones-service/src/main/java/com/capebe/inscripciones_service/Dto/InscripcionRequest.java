package com.capebe.inscripciones_service.Dto;

import jakarta.validation.constraints.*;

public record InscripcionRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long vueloId,
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @AssertTrue(message = "Primero debe aceptar el consentimiento para poder inscribirse.") Boolean consentimientoAceptado
) {
}

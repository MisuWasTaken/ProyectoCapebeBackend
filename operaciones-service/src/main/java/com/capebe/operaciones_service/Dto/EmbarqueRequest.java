package com.capebe.operaciones_service.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmbarqueRequest{

    @NotNull(message = "El ID del vuelo es obligatorio")
    private Long vueloId;

    @NotNull(message = "El ID de la inscripción es obligatorio")
    private Long inscripcionId;
}
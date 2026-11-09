package com.capebe.vuelos_reportes_service.Dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record VueloRequest(
        @NotNull
        @FutureOrPresent
        LocalDate fecha,

        @NotNull
        LocalTime hora,

        @NotNull
        @Min(1)
        Integer cupos,

        @NotBlank
        String origen,

        @NotBlank
        String destino
) {
}
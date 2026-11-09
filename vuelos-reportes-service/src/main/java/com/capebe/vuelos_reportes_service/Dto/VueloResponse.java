package com.capebe.vuelos_reportes_service.Dto;

import com.capebe.vuelos_reportes_service.Entity.EstadoVuelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record VueloResponse(
        Long id,
        LocalDate fecha,
        LocalTime hora,
        Integer cupos,
        EstadoVuelo estado,
        String origen,
        String destino,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
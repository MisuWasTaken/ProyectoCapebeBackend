package com.capebe.inscripciones_service.Dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DatosVuelo(Long id, LocalDate fecha, LocalTime hora, Integer cupos, String estado) {
}

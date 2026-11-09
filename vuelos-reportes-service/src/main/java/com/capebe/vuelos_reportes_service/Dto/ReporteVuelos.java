package com.capebe.vuelos_reportes_service.Dto;

public record ReporteVuelos(
        long totalVuelos,
        long vuelosDisponibles,
        long vuelosCerrados,
        long vuelosFinalizados,
        long vuelosCancelados
) {
}
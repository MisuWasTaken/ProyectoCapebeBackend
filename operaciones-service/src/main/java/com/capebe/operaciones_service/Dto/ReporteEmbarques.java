package com.capebe.operaciones_service.Dto;

public record ReporteEmbarques(
        long totalPendientes,
        long totalEmbarcados,
        long totalNoEmbarcados,
        long totalPasajeros
) {}
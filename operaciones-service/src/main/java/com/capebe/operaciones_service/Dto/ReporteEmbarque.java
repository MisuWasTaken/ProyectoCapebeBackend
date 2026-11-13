package com.capebe.operaciones_service.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReporteEmbarque{
    private Long vueloId;
    private long totalPendientes;
    private long totalEmbarcados;
    private long totalNoEmbarcados;
    private long totalPasajeros;
}

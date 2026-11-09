package com.capebe.vuelos_reportes_service.Service;

import com.capebe.vuelos_reportes_service.Dto.EstadoVueloRequest;
import com.capebe.vuelos_reportes_service.Dto.ReporteVuelos;
import com.capebe.vuelos_reportes_service.Dto.VueloRequest;
import com.capebe.vuelos_reportes_service.Dto.VueloResponse;

import java.util.List;

public interface VueloService {

    VueloResponse crear(VueloRequest request);

    List<VueloResponse> obtenerTodos();

    VueloResponse obtenerPorId(Long id);

    VueloResponse cambiarEstado(
            Long id,
            EstadoVueloRequest request
    );

    ReporteVuelos obtenerReporte();
}
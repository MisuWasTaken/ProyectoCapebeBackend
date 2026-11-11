package com.capebe.inscripciones_service.Service;

import com.capebe.inscripciones_service.Dto.SolicitudCambioVuelo;
import com.capebe.inscripciones_service.Dto.InscripcionRequest;
import com.capebe.inscripciones_service.Dto.InscripcionResponse;

import java.util.List;

public interface InscripcionService {
    InscripcionResponse crear(InscripcionRequest request);
    InscripcionResponse obtenerPorId(Long id);
    List<InscripcionResponse> obtenerPorUsuario(Long usuarioId);
    List<InscripcionResponse> obtenerPorEmail(String email);
    List<InscripcionResponse> obtenerPorVuelo(Long vueloId);
    long contarPorVuelo(Long vueloId);
    InscripcionResponse cambiarVuelo(Long id, SolicitudCambioVuelo request);
}

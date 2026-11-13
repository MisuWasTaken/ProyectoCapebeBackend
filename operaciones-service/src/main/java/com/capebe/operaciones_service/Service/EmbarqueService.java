package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Dto.EmbarqueRequest;
import com.capebe.operaciones_service.Dto.EmbarqueResponse;
import com.capebe.operaciones_service.Dto.EstadoEmbarqueRequest;
import com.capebe.operaciones_service.Dto.ReporteEmbarques;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmbarqueService {
    EmbarqueResponse registrarEmbarque(EmbarqueRequest request, MultipartFile foto);
    List<EmbarqueResponse> obtenerEmbarquesPorVuelo(Long vueloId);
    EmbarqueResponse cambiarEstado(Long id, EstadoEmbarqueRequest request);
    ReporteEmbarques obtenerReporte(Long vueloId);
}
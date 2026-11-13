package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Dto.EmbarqueRequest;
import com.capebe.operaciones_service.Dto.EmbarqueResponse;
import com.capebe.operaciones_service.Dto.EstadoEmbarqueRequest;
import com.capebe.operaciones_service.Dto.ReporteEmbarques;
import com.capebe.operaciones_service.Entity.Embarque;
import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import com.capebe.operaciones_service.Exception.EmbarqueDuplicadoException;
import com.capebe.operaciones_service.Exception.EmbarqueNoEncontradoException;
import com.capebe.operaciones_service.Repository.EmbarqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class EmbarqueServiceImpl implements EmbarqueService {

    private final EmbarqueRepository embarqueRepository;
    private final SupabaseService supabaseService;
    private final ConsultaInscripcion consultaInscripcion;

    public EmbarqueServiceImpl(EmbarqueRepository embarqueRepository, SupabaseService supabaseService,
                              ConsultaInscripcion consultaInscripcion) {
        this.embarqueRepository = embarqueRepository;
        this.supabaseService = supabaseService;
        this.consultaInscripcion = consultaInscripcion;
    }

    @Override
    @Transactional
    public EmbarqueResponse registrarEmbarque(EmbarqueRequest request, MultipartFile foto) {
        consultaInscripcion.validarInscripcion(request.vueloId(), request.inscripcionId());

        embarqueRepository.findByVueloIdAndInscripcionId(request.vueloId(), request.inscripcionId())
                .ifPresent(e -> {
                    throw new EmbarqueDuplicadoException("Ya existe un registro de embarque para esta inscripcion en este vuelo.");
                });

        String fotoUrl = foto != null ? supabaseService.subirFoto(foto) : null;

        Embarque embarque = new Embarque();
        embarque.setVueloId(request.vueloId());
        embarque.setInscripcionId(request.inscripcionId());
        embarque.setEstado(EstadoEmbarque.PENDIENTE);
        embarque.setFotoUrl(fotoUrl);

        return convertirRespuesta(embarqueRepository.save(embarque));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmbarqueResponse> obtenerEmbarquesPorVuelo(Long vueloId) {
        return embarqueRepository.findByVueloId(vueloId).stream().map(this::convertirRespuesta).toList();
    }

    @Override
    @Transactional
    public EmbarqueResponse cambiarEstado(Long id, EstadoEmbarqueRequest request) {
        Embarque embarque = buscarEmbarque(id);
        embarque.setEstado(request.estado());
        return convertirRespuesta(embarqueRepository.save(embarque));
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteEmbarques obtenerReporte(Long vueloId) {
        long pendientes = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.PENDIENTE);
        long embarcados = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.EMBARCADO);
        long noEmbarcados = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.NO_EMBARCADO);
        return new ReporteEmbarques(pendientes, embarcados, noEmbarcados, pendientes + embarcados + noEmbarcados);
    }

    private Embarque buscarEmbarque(Long id) {
        return embarqueRepository.findById(id)
                .orElseThrow(() -> new EmbarqueNoEncontradoException("Registro de embarque no encontrado."));
    }

    private EmbarqueResponse convertirRespuesta(Embarque embarque) {
        return new EmbarqueResponse(embarque.getId(), embarque.getVueloId(), embarque.getInscripcionId(),
                embarque.getEstado(), embarque.getFotoUrl(), embarque.getCreatedAt(), embarque.getUpdatedAt());
    }
}

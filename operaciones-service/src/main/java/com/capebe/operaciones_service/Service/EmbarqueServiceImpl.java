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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class EmbarqueServiceImpl implements EmbarqueService {

    private final EmbarqueRepository embarqueRepository;
    private final SupabaseService supabaseService;

    public EmbarqueServiceImpl(EmbarqueRepository embarqueRepository, SupabaseService supabaseService) {
        this.embarqueRepository = embarqueRepository;
        this.supabaseService = supabaseService;
    }

    @Override
    public EmbarqueResponse registrarEmbarque(EmbarqueRequest request, MultipartFile foto) {
        // Evitar duplicados (mismo vuelo y misma inscripción)
        embarqueRepository.findByVueloIdAndInscripcionId(request.vueloId(), request.inscripcionId())
                .ifPresent(e -> {
                    throw new EmbarqueDuplicadoException("Ya existe un registro de embarque para esta inscripción en este vuelo.");
                });

        String fotoUrl = null;
        if (foto != null && !foto.isEmpty()) {
            fotoUrl = supabaseService.subirFoto(foto);
        }

        Embarque embarque = new Embarque();
        embarque.setVueloId(request.vueloId());
        embarque.setInscripcionId(request.inscripcionId());
        embarque.setEstado(EstadoEmbarque.PENDIENTE);
        embarque.setFotoUrl(fotoUrl);

        Embarque guardado = embarqueRepository.save(embarque);

        return convertirRespuesta(guardado);
    }

    @Override
    public List<EmbarqueResponse> obtenerEmbarquesPorVuelo(Long vueloId) {
        return embarqueRepository.findByVueloId(vueloId)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    public EmbarqueResponse cambiarEstado(Long id, EstadoEmbarqueRequest request) {
        Embarque embarque = buscarEmbarque(id);

        embarque.setEstado(request.estado());

        return convertirRespuesta(
                embarqueRepository.save(embarque)
        );
    }

    @Override
    public ReporteEmbarques obtenerReporte(Long vueloId) {
        long pendientes = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.PENDIENTE);
        long embarcados = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.EMBARCADO);
        long noEmbarcados = embarqueRepository.countByVueloIdAndEstado(vueloId, EstadoEmbarque.NO_EMBARCADO);

        return new ReporteEmbarques(
                pendientes,
                embarcados,
                noEmbarcados,
                pendientes + embarcados + noEmbarcados
        );
    }

    private Embarque buscarEmbarque(Long id) {
        return embarqueRepository.findById(id)
                .orElseThrow(() -> new EmbarqueNoEncontradoException("Registro de embarque no encontrado."));
    }

    private EmbarqueResponse convertirRespuesta(Embarque embarque) {
        return new EmbarqueResponse(
                embarque.getId(),
                embarque.getVueloId(),
                embarque.getInscripcionId(),
                embarque.getEstado(),
                embarque.getFotoUrl(),
                embarque.getCreatedAt(),
                embarque.getUpdatedAt()
        );
    }
}
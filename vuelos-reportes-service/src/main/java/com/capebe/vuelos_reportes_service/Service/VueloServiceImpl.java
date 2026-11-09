package com.capebe.vuelos_reportes_service.Service;

import com.capebe.vuelos_reportes_service.Dto.EstadoVueloRequest;
import com.capebe.vuelos_reportes_service.Dto.ReporteVuelos;
import com.capebe.vuelos_reportes_service.Dto.VueloRequest;
import com.capebe.vuelos_reportes_service.Dto.VueloResponse;
import com.capebe.vuelos_reportes_service.Entity.EstadoVuelo;
import com.capebe.vuelos_reportes_service.Entity.Vuelo;
import com.capebe.vuelos_reportes_service.Exception.VueloInvalidoException;
import com.capebe.vuelos_reportes_service.Exception.VueloNoEncontradoException;
import com.capebe.vuelos_reportes_service.Repository.VuelosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VueloServiceImpl implements VueloService {

    private final VuelosRepository vuelosRepository;

    public VueloServiceImpl(VuelosRepository vuelosRepository) {
        this.vuelosRepository = vuelosRepository;
    }

    @Override
    public VueloResponse crear(VueloRequest request) {

        LocalDateTime fechaHora =
                LocalDateTime.of(request.fecha(), request.hora());

        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new VueloInvalidoException(
                    "La fecha y hora del vuelo deben ser posteriores a la actual."
            );
        }

        Vuelo vuelo = new Vuelo();

        vuelo.setFecha(request.fecha());
        vuelo.setHora(request.hora());
        vuelo.setCupos(request.cupos());
        vuelo.setOrigen(request.origen());
        vuelo.setDestino(request.destino());
        vuelo.setEstado(EstadoVuelo.DISPONIBLE);

        Vuelo guardado = vuelosRepository.save(vuelo);

        return convertirRespuesta(guardado);
    }

    @Override
    public List<VueloResponse> obtenerTodos() {

        return vuelosRepository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Override
    public VueloResponse obtenerPorId(Long id) {
        return convertirRespuesta(buscarVuelo(id));
    }

    @Override
    public VueloResponse cambiarEstado(
            Long id,
            EstadoVueloRequest request
    ) {

        Vuelo vuelo = buscarVuelo(id);

        vuelo.setEstado(request.estado());

        return convertirRespuesta(
                vuelosRepository.save(vuelo)
        );
    }

    @Override
    public ReporteVuelos obtenerReporte() {

        return new ReporteVuelos(
                vuelosRepository.count(),
                vuelosRepository.countByEstado(EstadoVuelo.DISPONIBLE),
                vuelosRepository.countByEstado(EstadoVuelo.CERRADO),
                vuelosRepository.countByEstado(EstadoVuelo.FINALIZADO),
                vuelosRepository.countByEstado(EstadoVuelo.CANCELADO)
        );
    }

    private Vuelo buscarVuelo(Long id) {

        return vuelosRepository.findById(id)
                .orElseThrow(() ->
                        new VueloNoEncontradoException(
                                "Vuelo no encontrado."
                        )
                );
    }

    private VueloResponse convertirRespuesta(Vuelo vuelo) {

        return new VueloResponse(
                vuelo.getId(),
                vuelo.getFecha(),
                vuelo.getHora(),
                vuelo.getCupos(),
                vuelo.getEstado(),
                vuelo.getOrigen(),
                vuelo.getDestino(),
                vuelo.getCreatedAt(),
                vuelo.getUpdatedAt()
        );
    }
}
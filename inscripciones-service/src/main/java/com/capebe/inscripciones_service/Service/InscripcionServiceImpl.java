package com.capebe.inscripciones_service.Service;

import com.capebe.inscripciones_service.Dto.SolicitudCambioVuelo;
import com.capebe.inscripciones_service.Dto.InscripcionRequest;
import com.capebe.inscripciones_service.Dto.InscripcionResponse;
import com.capebe.inscripciones_service.Dto.DatosVuelo;
import com.capebe.inscripciones_service.Entity.Inscripcion;
import com.capebe.inscripciones_service.Exception.ConflictoInscripcionException;
import com.capebe.inscripciones_service.Exception.ServicioVuelosException;
import com.capebe.inscripciones_service.Exception.NoEncontradoException;
import com.capebe.inscripciones_service.Repository.InscripcionesRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    private static final ZoneId ZONA_VUELOS = ZoneId.of("America/Santiago");
    private final InscripcionesRepository inscripcionesRepository;
    private final ConsultaVuelo vueloClient;
    private final BloquearVuelo vueloLock;

    public InscripcionServiceImpl(InscripcionesRepository inscripcionesRepository,
                                 ConsultaVuelo vueloClient, BloquearVuelo vueloLock) {
        this.inscripcionesRepository = inscripcionesRepository;
        this.vueloClient = vueloClient;
        this.vueloLock = vueloLock;
    }

    @Override
    @Transactional
    public InscripcionResponse crear(InscripcionRequest request) {
        if (!Boolean.TRUE.equals(request.consentimientoAceptado())) {
            throw new ConflictoInscripcionException("Primero debe aceptar el consentimiento para inscribirte.");
        }
        DatosVuelo vuelo = vueloClient.obtenerPorId(request.vueloId());
        validarDisponible(vuelo);

        vueloLock.bloquear(vuelo.id());
        String email = normalizarEmail(request.email());

        if (inscripcionesRepository.existsByEmailIgnoreCaseAndVueloId(email, vuelo.id())) {
            throw new ConflictoInscripcionException("Ya existe una inscripcion para este usuario y vuelo.");
        }
        comprobarCupos(vuelo);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuarioId(request.usuarioId());
        inscripcion.setVueloId(vuelo.id());
        inscripcion.setNombre(request.nombre().trim());
        inscripcion.setEmail(email);
        inscripcion.setConsentimientoAceptado(true);

        try {
            return convertir(inscripcionesRepository.saveAndFlush(inscripcion));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictoInscripcionException("La inscripcion ya existe o contiene datos invalidos.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtenerPorId(Long id) {
        return convertir(buscarInscripcion(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerPorUsuario(Long usuarioId) {
        return inscripcionesRepository.findByUsuarioIdOrderByIdAsc(usuarioId)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerPorEmail(String email) {
        return inscripcionesRepository.findByEmailIgnoreCaseOrderByIdAsc(normalizarEmail(email))
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerPorVuelo(Long vueloId) {
        return inscripcionesRepository.findByVueloIdOrderByIdAsc(vueloId)
                .stream().map(this::convertir).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorVuelo(Long vueloId) {
        return inscripcionesRepository.countByVueloId(vueloId);
    }

    @Override
    @Transactional
    public InscripcionResponse cambiarVuelo(Long id, SolicitudCambioVuelo request) {
        Inscripcion inscripcion = buscarInscripcion(id);

        if (inscripcion.getVueloId().equals(request.nuevoVueloId())) {
            throw new ConflictoInscripcionException("El pasajero ya esta inscrito en ese vuelo.");
        }

        DatosVuelo vueloActual = vueloClient.obtenerPorId(inscripcion.getVueloId());
        DatosVuelo nuevoVuelo = vueloClient.obtenerPorId(request.nuevoVueloId());
        validarDisponible(nuevoVuelo);

        LocalDateTime actual = LocalDateTime.of(vueloActual.fecha(), vueloActual.hora());
        LocalDateTime nuevo = LocalDateTime.of(nuevoVuelo.fecha(), nuevoVuelo.hora());
        if (!nuevo.isAfter(actual)) {
            throw new ConflictoInscripcionException("El nuevo vuelo debe tener fecha y hora posteriores a la actual.");
        }

        vueloLock.bloquear(nuevoVuelo.id());
        if (inscripcionesRepository.existsByEmailIgnoreCaseAndVueloId(inscripcion.getEmail(), nuevoVuelo.id())) {
            throw new ConflictoInscripcionException("El pasajero ya esta inscrito en el vuelo de destino.");
        }
        comprobarCupos(nuevoVuelo);

        inscripcion.setVueloId(nuevoVuelo.id());
        try {
            return convertir(inscripcionesRepository.saveAndFlush(inscripcion));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictoInscripcionException("No fue posible cambiar el vuelo por datos duplicados.");
        }
    }

    private void validarDisponible(DatosVuelo vuelo) {
        if (vuelo.id() == null || vuelo.fecha() == null || vuelo.hora() == null || vuelo.cupos() == null) {
            throw new ServicioVuelosException("El servicio de vuelos devolvio datos incompletos.");
        }
        if (!"DISPONIBLE".equals(vuelo.estado())) {
            throw new ConflictoInscripcionException("El vuelo no esta disponible para inscripciones.");
        }
        LocalDateTime fechaHora = LocalDateTime.of(vuelo.fecha(), vuelo.hora());
        if (!fechaHora.isAfter(LocalDateTime.now(ZONA_VUELOS))) {
            throw new ConflictoInscripcionException("No es posible inscribirse a un vuelo pasado.");
        }
    }

    private void comprobarCupos(DatosVuelo vuelo) {
        if (inscripcionesRepository.countByVueloId(vuelo.id()) >= vuelo.cupos()) {
            throw new ConflictoInscripcionException("El vuelo no tiene cupos disponibles.");
        }
    }

    private Inscripcion buscarInscripcion(Long id) {
        return inscripcionesRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Inscripcion no encontrada."));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private InscripcionResponse convertir(Inscripcion inscripcion) {
        return new InscripcionResponse(inscripcion.getId(), inscripcion.getUsuarioId(),
                inscripcion.getVueloId(), inscripcion.getNombre(), inscripcion.getEmail(),
                inscripcion.getConsentimientoAceptado(), inscripcion.getCreatedAt(), inscripcion.getUpdatedAt());
    }
}

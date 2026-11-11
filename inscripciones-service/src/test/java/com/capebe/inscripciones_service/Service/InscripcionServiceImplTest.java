package com.capebe.inscripciones_service.Service;

import com.capebe.inscripciones_service.Dto.SolicitudCambioVuelo;
import com.capebe.inscripciones_service.Dto.InscripcionRequest;
import com.capebe.inscripciones_service.Dto.InscripcionResponse;
import com.capebe.inscripciones_service.Dto.DatosVuelo;
import com.capebe.inscripciones_service.Entity.Inscripcion;
import com.capebe.inscripciones_service.Exception.ConflictoInscripcionException;
import com.capebe.inscripciones_service.Exception.NoEncontradoException;
import com.capebe.inscripciones_service.Repository.InscripcionesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceImplTest {

    @Mock private InscripcionesRepository inscripcionesRepository;
    @Mock private ConsultaVuelo vueloClient;
    @Mock private BloquearVuelo vueloLock;
    @InjectMocks private InscripcionServiceImpl inscripcionService;

    @Test
    public void crearInscripcionCorrectamente() {
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 2, "DISPONIBLE", 2));
        when(inscripcionesRepository.saveAndFlush(any(Inscripcion.class))).thenAnswer(invocation -> {
            Inscripcion inscripcion = invocation.getArgument(0);
            inscripcion.setId(1L);
            return inscripcion;
        });

        InscripcionResponse respuesta = inscripcionService.crear(solicitud());

        assertEquals(1L, respuesta.id());
        assertEquals("invitado@ejemplo.cl", respuesta.email());
        assertTrue(respuesta.consentimientoAceptado());
        verify(vueloLock).bloquear(1L);
        verify(inscripcionesRepository).saveAndFlush(any(Inscripcion.class));
    }

    @Test
    public void rechazarInscripcionDuplicada() {
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 2, "DISPONIBLE", 2));
        when(inscripcionesRepository.existsByEmailIgnoreCaseAndVueloId("invitado@ejemplo.cl", 1L))
                .thenReturn(true);

        assertThrows(ConflictoInscripcionException.class, () -> inscripcionService.crear(solicitud()));
        verify(inscripcionesRepository, never()).saveAndFlush(any());
    }

    @Test
    public void rechazarVueloSinCupos() {
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 1, "DISPONIBLE", 2));
        when(inscripcionesRepository.countByVueloId(1L)).thenReturn(1L);

        assertThrows(ConflictoInscripcionException.class, () -> inscripcionService.crear(solicitud()));
        verify(inscripcionesRepository, never()).saveAndFlush(any());
    }

    @Test
    public void rechazarVueloCerrado() {
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 2, "CERRADO", 2));

        assertThrows(ConflictoInscripcionException.class, () -> inscripcionService.crear(solicitud()));
        verifyNoInteractions(inscripcionesRepository);
    }

    @Test
    public void rechazarVueloPasado() {
        when(vueloClient.obtenerPorId(1L)).thenReturn(new DatosVuelo(
                1L, LocalDate.now(ZoneId.of("America/Santiago")).minusDays(2), LocalTime.of(15, 30), 10, "DISPONIBLE"));

        assertThrows(ConflictoInscripcionException.class, () -> inscripcionService.crear(solicitud()));
        verifyNoInteractions(inscripcionesRepository);
    }

    @Test
    public void rechazarSinConsentimiento() {
        InscripcionRequest request = new InscripcionRequest(3L, 1L, "Invitado", "invitado@ejemplo.cl", false);
        assertThrows(ConflictoInscripcionException.class, () -> inscripcionService.crear(request));
        verifyNoInteractions(vueloClient, inscripcionesRepository);
    }

    @Test
    public void listarInscripcionesPorVuelo() {
        when(inscripcionesRepository.findByVueloIdOrderByIdAsc(1L)).thenReturn(List.of(inscripcion(1L, 1L)));

        List<InscripcionResponse> lista = inscripcionService.obtenerPorVuelo(1L);
        assertEquals(1, lista.size());
        assertEquals(1L, lista.get(0).vueloId());
    }

    @Test
    public void contarInscritos() {
        when(inscripcionesRepository.countByVueloId(1L)).thenReturn(4L);
        assertEquals(4, inscripcionService.contarPorVuelo(1L));
    }

    @Test
    public void buscarInscripcionInexistente() {
        when(inscripcionesRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoEncontradoException.class, () -> inscripcionService.obtenerPorId(99L));
    }

    @Test
    public void cambiarVueloCorrectamente() {
        Inscripcion inscripcion = inscripcion(1L, 1L);
        when(inscripcionesRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 3, "DISPONIBLE", 2));
        when(vueloClient.obtenerPorId(2L)).thenReturn(vuelo(2L, 3, "DISPONIBLE", 5));
        when(inscripcionesRepository.saveAndFlush(any(Inscripcion.class))).thenAnswer(i -> i.getArgument(0));

        InscripcionResponse respuesta = inscripcionService.cambiarVuelo(1L, new SolicitudCambioVuelo(2L));

        assertEquals(2L, respuesta.vueloId());
        verify(vueloLock).bloquear(2L);
    }

    @Test
    public void rechazarCambioAVueloAnterior() {
        when(inscripcionesRepository.findById(1L)).thenReturn(Optional.of(inscripcion(1L, 1L)));
        when(vueloClient.obtenerPorId(1L)).thenReturn(vuelo(1L, 3, "DISPONIBLE", 5));
        when(vueloClient.obtenerPorId(2L)).thenReturn(vuelo(2L, 3, "DISPONIBLE", 2));

        assertThrows(ConflictoInscripcionException.class,
                () -> inscripcionService.cambiarVuelo(1L, new SolicitudCambioVuelo(2L)));
        verify(inscripcionesRepository, never()).saveAndFlush(any());
    }

    private InscripcionRequest solicitud() {
        return new InscripcionRequest(3L, 1L, "Invitado", "Invitado@Ejemplo.cl", true);
    }

    private DatosVuelo vuelo(Long id, int cupos, String estado, int diasEnFuturo) {
        return new DatosVuelo(id, LocalDate.now(ZoneId.of("America/Santiago")).plusDays(diasEnFuturo), LocalTime.of(15, 30), cupos, estado);
    }

    private Inscripcion inscripcion(Long id, Long vueloId) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(id);
        inscripcion.setUsuarioId(3L);
        inscripcion.setVueloId(vueloId);
        inscripcion.setNombre("Invitado");
        inscripcion.setEmail("invitado@ejemplo.cl");
        inscripcion.setConsentimientoAceptado(true);
        return inscripcion;
    }
}

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VueloServiceImplTest {

    @Mock
    private VuelosRepository vuelosRepository;

    @InjectMocks
    private VueloServiceImpl vueloService;

    @Test
    public void crearVueloCorrectamente() {

        VueloRequest request = new VueloRequest(
                LocalDate.now().plusDays(2),
                LocalTime.of(15, 30),
                20,
                "Santiago",
                "Talcahuano"
        );

        Vuelo vueloGuardado = crearVueloPrueba(1L, EstadoVuelo.DISPONIBLE);

        when(vuelosRepository.save(any(Vuelo.class))).thenReturn(vueloGuardado);

        VueloResponse respuesta = vueloService.crear(request);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(20, respuesta.cupos());
        assertEquals(EstadoVuelo.DISPONIBLE, respuesta.estado());

        verify(vuelosRepository, times(1)).save(any(Vuelo.class));
    }

    @Test
    public void rechazarVueloConFechaPasada() {

        VueloRequest request = new VueloRequest(
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                20,
                "Santiago",
                "Talcahuano"
        );

        assertThrows(VueloInvalidoException.class, () -> vueloService.crear(request));

        verify(vuelosRepository, never()).save(any(Vuelo.class));
    }

    @Test
    public void obtenerTodosLosVuelos() {

        Vuelo vuelo1 = crearVueloPrueba(1L, EstadoVuelo.DISPONIBLE);
        Vuelo vuelo2 = crearVueloPrueba(2L, EstadoVuelo.CERRADO);

        when(vuelosRepository.findAll()).thenReturn(List.of(vuelo1, vuelo2));

        List<VueloResponse> vuelos = vueloService.obtenerTodos();

        assertEquals(2, vuelos.size());
        verify(vuelosRepository, times(1)).findAll();
    }

    @Test
    public void obtenerVueloPorId() {

        Vuelo vuelo = crearVueloPrueba(1L, EstadoVuelo.DISPONIBLE);

        when(vuelosRepository.findById(1L)).thenReturn(Optional.of(vuelo));

        VueloResponse respuesta = vueloService.obtenerPorId(1L);

        assertEquals(1L, respuesta.id());
        assertEquals(EstadoVuelo.DISPONIBLE, respuesta.estado());
    }

    @Test
    public void errorCuandoVueloNoExiste() {

        when(vuelosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                VueloNoEncontradoException.class,
                () -> vueloService.obtenerPorId(99L)
        );
    }

    @Test
    public void cambiarEstadoCorrectamente() {

        Vuelo vuelo = crearVueloPrueba(1L, EstadoVuelo.DISPONIBLE);

        when(vuelosRepository.findById(1L)).thenReturn(Optional.of(vuelo));
        when(vuelosRepository.save(any(Vuelo.class))).thenReturn(vuelo);

        EstadoVueloRequest request = new EstadoVueloRequest(EstadoVuelo.CERRADO);

        VueloResponse respuesta = vueloService.cambiarEstado(1L, request);

        assertEquals(EstadoVuelo.CERRADO, respuesta.estado());
        verify(vuelosRepository, times(1)).save(vuelo);
    }

    @Test
    public void generarReporteCorrectamente() {

        when(vuelosRepository.count()).thenReturn(4L);
        when(vuelosRepository.countByEstado(EstadoVuelo.DISPONIBLE)).thenReturn(1L);
        when(vuelosRepository.countByEstado(EstadoVuelo.CERRADO)).thenReturn(1L);
        when(vuelosRepository.countByEstado(EstadoVuelo.FINALIZADO)).thenReturn(1L);
        when(vuelosRepository.countByEstado(EstadoVuelo.CANCELADO)).thenReturn(1L);

        ReporteVuelos reporte = vueloService.obtenerReporte();

        assertEquals(4, reporte.totalVuelos());
        assertEquals(1, reporte.vuelosDisponibles());
        assertEquals(1, reporte.vuelosCerrados());
        assertEquals(1, reporte.vuelosFinalizados());
        assertEquals(1, reporte.vuelosCancelados());
    }

    private Vuelo crearVueloPrueba(Long id, EstadoVuelo estado) {

        Vuelo vuelo = new Vuelo();

        vuelo.setId(id);
        vuelo.setFecha(LocalDate.now().plusDays(2));
        vuelo.setHora(LocalTime.of(15, 30));
        vuelo.setCupos(20);
        vuelo.setEstado(estado);
        vuelo.setOrigen("Santiago");
        vuelo.setDestino("Talcahuano");
        vuelo.setCreatedAt(LocalDateTime.now());
        vuelo.setUpdatedAt(LocalDateTime.now());

        return vuelo;
    }
}
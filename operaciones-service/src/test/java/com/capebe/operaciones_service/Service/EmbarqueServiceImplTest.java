package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Dto.EmbarqueRequest;
import com.capebe.operaciones_service.Dto.EmbarqueResponse;
import com.capebe.operaciones_service.Dto.EstadoEmbarqueRequest;
import com.capebe.operaciones_service.Dto.ReporteEmbarques;
import com.capebe.operaciones_service.Entity.Embarque;
import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import com.capebe.operaciones_service.Exception.EmbarqueDuplicadoException;
import com.capebe.operaciones_service.Exception.EmbarqueNoEncontradoException;
import com.capebe.operaciones_service.Exception.InscripcionNoValidaException;
import com.capebe.operaciones_service.Repository.EmbarqueRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmbarqueServiceImplTest {

    @Mock
    private EmbarqueRepository embarqueRepository;

    @Mock
    private SupabaseService supabaseService;

    @Mock
    private ConsultaInscripcion consultaInscripcion;

    @InjectMocks
    private EmbarqueServiceImpl embarqueService;

    @Test
    public void registrarEmbarqueCorrectamente() {
        EmbarqueRequest request = new EmbarqueRequest(1L, 100L);
        MockMultipartFile foto = new MockMultipartFile("foto", "test.jpg", "image/jpeg", "bytes".getBytes());

        doNothing().when(consultaInscripcion).validarInscripcion(1L, 100L);
        when(embarqueRepository.findByVueloIdAndInscripcionId(1L, 100L)).thenReturn(Optional.empty());
        when(supabaseService.subirFoto(foto)).thenReturn("https://supabase.co/foto.jpg");

        Embarque embarqueGuardado = crearEmbarquePrueba(1L, 1L, 100L, EstadoEmbarque.PENDIENTE, "https://supabase.co/foto.jpg");
        when(embarqueRepository.save(any(Embarque.class))).thenReturn(embarqueGuardado);

        EmbarqueResponse respuesta = embarqueService.registrarEmbarque(request, foto);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(1L, respuesta.vueloId());
        assertEquals(100L, respuesta.inscripcionId());
        assertEquals(EstadoEmbarque.PENDIENTE, respuesta.estado());
        assertEquals("https://supabase.co/foto.jpg", respuesta.fotoUrl());

        verify(consultaInscripcion, times(1)).validarInscripcion(1L, 100L);
        verify(embarqueRepository, times(1)).save(any(Embarque.class));
    }

    @Test
    public void rechazarPasajeroNoInscrito() {
        EmbarqueRequest request = new EmbarqueRequest(1L, 999L);

        doThrow(new InscripcionNoValidaException("El pasajero no esta inscrito."))
                .when(consultaInscripcion).validarInscripcion(1L, 999L);

        assertThrows(InscripcionNoValidaException.class, () -> embarqueService.registrarEmbarque(request, null));

        verify(embarqueRepository, never()).save(any(Embarque.class));
    }

    @Test
    public void rechazarEmbarqueDuplicado() {
        EmbarqueRequest request = new EmbarqueRequest(1L, 100L);
        Embarque embarqueExistente = crearEmbarquePrueba(1L, 1L, 100L, EstadoEmbarque.PENDIENTE, null);

        doNothing().when(consultaInscripcion).validarInscripcion(1L, 100L);
        when(embarqueRepository.findByVueloIdAndInscripcionId(1L, 100L)).thenReturn(Optional.of(embarqueExistente));

        assertThrows(EmbarqueDuplicadoException.class, () -> embarqueService.registrarEmbarque(request, null));

        verify(embarqueRepository, never()).save(any(Embarque.class));
    }

    @Test
    public void obtenerEmbarquesPorVueloTest() {
        Embarque embarque1 = crearEmbarquePrueba(1L, 1L, 100L, EstadoEmbarque.PENDIENTE, null);
        Embarque embarque2 = crearEmbarquePrueba(2L, 1L, 101L, EstadoEmbarque.EMBARCADO, null);

        when(embarqueRepository.findByVueloId(1L)).thenReturn(List.of(embarque1, embarque2));

        List<EmbarqueResponse> resultado = embarqueService.obtenerEmbarquesPorVuelo(1L);

        assertEquals(2, resultado.size());
        verify(embarqueRepository, times(1)).findByVueloId(1L);
    }

    @Test
    public void cambiarEstadoCorrectamente() {
        Embarque embarque = crearEmbarquePrueba(1L, 1L, 100L, EstadoEmbarque.PENDIENTE, null);

        when(embarqueRepository.findById(1L)).thenReturn(Optional.of(embarque));
        when(embarqueRepository.save(any(Embarque.class))).thenReturn(embarque);

        EstadoEmbarqueRequest request = new EstadoEmbarqueRequest(EstadoEmbarque.EMBARCADO);

        EmbarqueResponse respuesta = embarqueService.cambiarEstado(1L, request);

        assertEquals(EstadoEmbarque.EMBARCADO, respuesta.estado());
        verify(embarqueRepository, times(1)).save(embarque);
    }

    @Test
    public void errorCuandoEmbarqueNoExisteAlCambiarEstado() {
        when(embarqueRepository.findById(99L)).thenReturn(Optional.empty());

        EstadoEmbarqueRequest request = new EstadoEmbarqueRequest(EstadoEmbarque.EMBARCADO);

        assertThrows(
                EmbarqueNoEncontradoException.class,
                () -> embarqueService.cambiarEstado(99L, request)
        );
    }

    @Test
    public void generarReporteCorrectamente() {
        when(embarqueRepository.countByVueloIdAndEstado(1L, EstadoEmbarque.PENDIENTE)).thenReturn(5L);
        when(embarqueRepository.countByVueloIdAndEstado(1L, EstadoEmbarque.EMBARCADO)).thenReturn(10L);
        when(embarqueRepository.countByVueloIdAndEstado(1L, EstadoEmbarque.NO_EMBARCADO)).thenReturn(2L);

        ReporteEmbarques reporte = embarqueService.obtenerReporte(1L);

        assertEquals(5, reporte.totalPendientes());
        assertEquals(10, reporte.totalEmbarcados());
        assertEquals(2, reporte.totalNoEmbarcados());
        assertEquals(17, reporte.totalPasajeros());
    }

    private Embarque crearEmbarquePrueba(Long id, Long vueloId, Long inscripcionId, EstadoEmbarque estado, String fotoUrl) {
        Embarque embarque = new Embarque();
        embarque.setId(id);
        embarque.setVueloId(vueloId);
        embarque.setInscripcionId(inscripcionId);
        embarque.setEstado(estado);
        embarque.setFotoUrl(fotoUrl);
        embarque.setCreatedAt(OffsetDateTime.now());
        embarque.setUpdatedAt(OffsetDateTime.now());
        return embarque;
    }
}
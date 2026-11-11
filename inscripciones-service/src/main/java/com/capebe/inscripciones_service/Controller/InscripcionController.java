package com.capebe.inscripciones_service.Controller;

import com.capebe.inscripciones_service.Dto.SolicitudCambioVuelo;
import com.capebe.inscripciones_service.Dto.InscripcionRequest;
import com.capebe.inscripciones_service.Dto.InscripcionResponse;
import com.capebe.inscripciones_service.Service.InscripcionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse crear(@Valid @RequestBody InscripcionRequest request) {
        return inscripcionService.crear(request);
    }

    @GetMapping("/{id}")
    public InscripcionResponse obtenerPorId(@PathVariable @Positive Long id) {
        return inscripcionService.obtenerPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<InscripcionResponse> obtenerPorUsuario(@PathVariable @Positive Long usuarioId) {
        return inscripcionService.obtenerPorUsuario(usuarioId);
    }

    @GetMapping("/usuario")
    public List<InscripcionResponse> obtenerPorEmail(@RequestParam @NotBlank @Email String email) {
        return inscripcionService.obtenerPorEmail(email);
    }

    @GetMapping("/vuelo/{vueloId}")
    public List<InscripcionResponse> obtenerPorVuelo(@PathVariable @Positive Long vueloId) {
        return inscripcionService.obtenerPorVuelo(vueloId);
    }

    @GetMapping("/vuelo/{vueloId}/cantidad")
    public long contarPorVuelo(@PathVariable @Positive Long vueloId) {
        return inscripcionService.contarPorVuelo(vueloId);
    }

    @PatchMapping("/{id}/vuelo")
    public InscripcionResponse cambiarVuelo(@PathVariable @Positive Long id,
                                             @Valid @RequestBody SolicitudCambioVuelo request) {
        return inscripcionService.cambiarVuelo(id, request);
    }
}

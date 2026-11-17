
package com.capebe.inscripciones_service.Controller;

import com.capebe.inscripciones_service.Dto.SolicitudCambioVuelo;
import com.capebe.inscripciones_service.Dto.InscripcionRequest;
import com.capebe.inscripciones_service.Dto.InscripcionResponse;
import com.capebe.inscripciones_service.Service.InscripcionService;
import com.capebe.inscripciones_service.Service.SupabaseUsuarioClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final SupabaseUsuarioClient supabaseUsuarioClient;

    public InscripcionController(
            InscripcionService inscripcionService,
            SupabaseUsuarioClient supabaseUsuarioClient
    ) {
        this.inscripcionService = inscripcionService;
        this.supabaseUsuarioClient = supabaseUsuarioClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse crear(
            @Valid @RequestBody InscripcionRequest request,
            @RequestHeader(value = "Authorization", required = false) String autorizacion
    ) {
        var usuario = supabaseUsuarioClient.autenticar(autorizacion);

        if (!usuario.email().equalsIgnoreCase(request.email().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El correo no corresponde a tu cuenta."
            );
        }

        return inscripcionService.crear(request, usuario.id());
    }

    @GetMapping("/{id}")
    public InscripcionResponse obtenerPorId(@PathVariable @Positive Long id) {
        return inscripcionService.obtenerPorId(id);
    }

    @GetMapping("/usuario")
    public List<InscripcionResponse> obtenerPorEmail(
            @RequestParam @NotBlank @Email String email,
            @RequestHeader(value = "Authorization", required = false) String autorizacion
    ) {
        var usuario = supabaseUsuarioClient.autenticar(autorizacion);

        if (!usuario.email().equalsIgnoreCase(email.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No puedes consultar otra cuenta."
            );
        }

        return inscripcionService.obtenerPorUsuarioAuthId(usuario.id());
    }

    @GetMapping("/vuelo/{vueloId}")
    public List<InscripcionResponse> obtenerPorVuelo(
            @PathVariable @Positive Long vueloId
    ) {
        return inscripcionService.obtenerPorVuelo(vueloId);
    }

    @GetMapping("/vuelo/{vueloId}/cantidad")
    public long contarPorVuelo(@PathVariable @Positive Long vueloId) {
        return inscripcionService.contarPorVuelo(vueloId);
    }

    @PatchMapping("/{id}/vuelo")
    public InscripcionResponse cambiarVuelo(
            @PathVariable @Positive Long id,
            @Valid @RequestBody SolicitudCambioVuelo request
    ) {
        return inscripcionService.cambiarVuelo(id, request);
    }
}

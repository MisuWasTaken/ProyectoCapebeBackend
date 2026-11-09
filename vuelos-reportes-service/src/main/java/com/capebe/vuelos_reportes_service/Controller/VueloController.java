package com.capebe.vuelos_reportes_service.Controller;

import com.capebe.vuelos_reportes_service.Dto.EstadoVueloRequest;
import com.capebe.vuelos_reportes_service.Dto.ReporteVuelos;
import com.capebe.vuelos_reportes_service.Dto.VueloRequest;
import com.capebe.vuelos_reportes_service.Dto.VueloResponse;
import com.capebe.vuelos_reportes_service.Service.VueloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vuelos")
public class VueloController {

    private final VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VueloResponse crear(
            @Valid @RequestBody VueloRequest request
    ) {
        return vueloService.crear(request);
    }

    @GetMapping
    public List<VueloResponse> obtenerTodos() {
        return vueloService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public VueloResponse obtenerPorId(
            @PathVariable Long id
    ) {
        return vueloService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/estado")
    public VueloResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoVueloRequest request
    ) {
        return vueloService.cambiarEstado(id, request);
    }

    @GetMapping("/reporte")
    public ReporteVuelos obtenerReporte() {
        return vueloService.obtenerReporte();
    }
}
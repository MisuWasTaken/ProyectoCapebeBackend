package com.capebe.operaciones_service.Controller;

import com.capebe.operaciones_service.Dto.EmbarqueRequest;
import com.capebe.operaciones_service.Dto.EmbarqueResponse;
import com.capebe.operaciones_service.Dto.EstadoEmbarqueRequest;
import com.capebe.operaciones_service.Dto.ReporteEmbarques;
import com.capebe.operaciones_service.Service.EmbarqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/embarques")
public class EmbarqueController {

    private final EmbarqueService embarqueService;

    public EmbarqueController(EmbarqueService embarqueService) {
        this.embarqueService = embarqueService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmbarqueResponse> registrarEmbarque(
            @RequestPart("data") @Valid EmbarqueRequest request,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        EmbarqueResponse response = embarqueService.registrarEmbarque(request, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/vuelo/{vueloId}")
    public ResponseEntity<List<EmbarqueResponse>> obtenerEmbarquesPorVuelo(@PathVariable Long vueloId) {
        List<EmbarqueResponse> response = embarqueService.obtenerEmbarquesPorVuelo(vueloId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reportes/vuelo/{vueloId}")
    public ResponseEntity<ReporteEmbarques> obtenerReporte(@PathVariable Long vueloId) {
        ReporteEmbarques reporte = embarqueService.obtenerReporte(vueloId);
        return ResponseEntity.ok(reporte);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EmbarqueResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody @Valid EstadoEmbarqueRequest request) {
        EmbarqueResponse response = embarqueService.cambiarEstado(id, request);
        return ResponseEntity.ok(response);
    }
}
package com.capebe.operaciones_service.Controller;

import com.capebe.operaciones_service.Dto.EmbarqueRequest;
import com.capebe.operaciones_service.Dto.EmbarqueResponse;
import com.capebe.operaciones_service.Dto.EstadoEmbarqueRequest;
import com.capebe.operaciones_service.Dto.ReporteEmbarques;
import com.capebe.operaciones_service.Service.EmbarqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @ResponseStatus(HttpStatus.CREATED)
    public EmbarqueResponse registrarEmbarque(@Valid @RequestPart("data") EmbarqueRequest request,
                                              @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return embarqueService.registrarEmbarque(request, foto);
    }

    @GetMapping("/vuelo/{vueloId}")
    public List<EmbarqueResponse> obtenerEmbarquesPorVuelo(@PathVariable Long vueloId) {
        return embarqueService.obtenerEmbarquesPorVuelo(vueloId);
    }

    @GetMapping("/reportes/vuelo/{vueloId}")
    public ReporteEmbarques obtenerReporte(@PathVariable Long vueloId) {
        return embarqueService.obtenerReporte(vueloId);
    }

    @PatchMapping("/{id}/estado")
    public EmbarqueResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoEmbarqueRequest request) {
        return embarqueService.cambiarEstado(id, request);
    }
}
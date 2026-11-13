package com.capebe.operaciones_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmbarqueNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse manejarEmbarqueNoEncontrado(EmbarqueNoEncontradoException e) {
        return new ErrorResponse(404, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(EmbarqueDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse manejarEmbarqueDuplicado(EmbarqueDuplicadoException e) {
        return new ErrorResponse(409, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InscripcionNoValidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarInscripcionNoValida(InscripcionNoValidaException e) {
        return new ErrorResponse(400, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ServicioInscripcionesException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse manejarServicioInscripciones(ServicioInscripcionesException e) {
        return new ErrorResponse(503, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(SupabaseStorageException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse manejarSupabaseStorage(SupabaseStorageException e) {
        return new ErrorResponse(503, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream().findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos invalidos.");
        return new ErrorResponse(400, mensaje, LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarJsonInvalido() {
        return new ErrorResponse(400, "JSON invalido o valor no permitido.", LocalDateTime.now());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarFotoGrande() {
        return new ErrorResponse(400, "La foto no puede superar los 5 MB.", LocalDateTime.now());
    }
}

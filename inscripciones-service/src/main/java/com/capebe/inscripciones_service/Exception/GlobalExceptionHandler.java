package com.capebe.inscripciones_service.Exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse manejarNoEncontrado(NoEncontradoException e) {
        return new ErrorResponse(404, e.getMessage(), OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(ConflictoInscripcionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse manejarConflicto(ConflictoInscripcionException e) {
        return new ErrorResponse(409, e.getMessage(), OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(ServicioVuelosException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse manejarDependencia(ServicioVuelosException e) {
        return new ErrorResponse(503, e.getMessage(), OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream().findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos invalidos.");
        return new ErrorResponse(400, mensaje, OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarParametros(ConstraintViolationException e) {
        return new ErrorResponse(400, "Parametro invalido.", OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarJson() {
        return new ErrorResponse(400, "JSON invalido o incompleto.", OffsetDateTime.now(ZoneOffset.UTC));
    }
}

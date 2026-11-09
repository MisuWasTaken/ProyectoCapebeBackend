package com.capebe.vuelos_reportes_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VueloNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse manejarVueloNoEncontrado(VueloNoEncontradoException e) {
        return new ErrorResponse(404, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarValidacion(MethodArgumentNotValidException e) {

        String mensaje = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos invalidos.");

        return new ErrorResponse(400, mensaje, LocalDateTime.now());
    }

    @ExceptionHandler(VueloInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarVueloInvalido(VueloInvalidoException e) {
        return new ErrorResponse(400, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse manejarJsonInvalido() {
        return new ErrorResponse(
                400,
                "JSON invalido o valor de estado no permitido.",
                LocalDateTime.now()
        );
    }
}
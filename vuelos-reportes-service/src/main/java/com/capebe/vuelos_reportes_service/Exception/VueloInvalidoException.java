package com.capebe.vuelos_reportes_service.Exception;

public class VueloInvalidoException extends RuntimeException {

    public VueloInvalidoException(String mensaje) {
        super(mensaje);
    }
}
package com.capebe.vuelos_reportes_service.Exception;

public class VueloNoEncontradoException extends RuntimeException {
    
    public VueloNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
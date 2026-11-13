package com.capebe.operaciones_service.Exception;

public class SupabaseStorageException extends RuntimeException {
    public SupabaseStorageException(String message, Throwable cause) {
        super(message, cause);
    }
    public SupabaseStorageException(String message) {
        super(message);
    }
}
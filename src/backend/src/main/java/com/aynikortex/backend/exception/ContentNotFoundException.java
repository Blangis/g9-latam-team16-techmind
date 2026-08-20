package com.aynikortex.backend.exception;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(UUID id) {
        super("No se encontró el contenido con id: " + id);
    }
}
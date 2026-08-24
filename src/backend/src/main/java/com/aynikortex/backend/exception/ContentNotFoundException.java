package com.aynikortex.backend.exception;

import java.util.UUID;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(UUID id) {
        super("No se encontró el contenido con id: " + id);
    }
}
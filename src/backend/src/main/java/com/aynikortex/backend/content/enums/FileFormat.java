package com.aynikortex.backend.content.enums;

public enum FileFormat {
    PDF,
    TXT,
    DOCX,
    MARKDOWN;

    public static FileFormat fromFilename(String filename) {

        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException(
                    "El archivo no tiene una extensión válida."
            );
        }

        String extension = filename
                .substring(filename.lastIndexOf('.') + 1)
                .toLowerCase();

        return switch (extension) {
            case "pdf" -> PDF;
            case "txt" -> TXT;
            case "docx" -> DOCX;
            case "md" -> MARKDOWN;
            default -> throw new IllegalArgumentException(
                    "Formato de archivo no soportado: " + extension
            );
        };
    }
}
package com.aynikortex.backend.content.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path storageLocation;

    public LocalFileStorageService(
            @Value("${file.storage.location}") String storageLocation) {

        this.storageLocation = Paths.get(storageLocation)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo crear el directorio de almacenamiento",
                    e
            );
        }
    }

    @Override
    public String save(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException(
                    "El archivo no tiene un nombre válido"
            );
        }

        String fileName = UUID.randomUUID() + "_" + originalFileName;

        Path targetLocation = storageLocation.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), targetLocation);
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo guardar el archivo",
                    e
            );
        }

        return targetLocation.toString();
    }

    @Override
    public void delete(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            Path file = Paths.get(filePath);

            Files.deleteIfExists(file);

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo eliminar el archivo",
                    e
            );
        }
    }
}
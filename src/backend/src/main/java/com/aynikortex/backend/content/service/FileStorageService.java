package com.aynikortex.backend.service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String save(MultipartFile file);

    void delete(String filePath);

    Resource load(String filePath);
}

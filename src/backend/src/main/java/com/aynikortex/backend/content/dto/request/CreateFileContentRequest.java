package com.aynikortex.backend.content.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CreateFileContentRequest(

        @Size(max = 200)
        String title,

        @NotNull(message = "Debe seleccionar un archivo.")
        MultipartFile file

) {}
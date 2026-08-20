package com.aynikortex.backend.content.dto.response;

import com.aynikortex.backend.content.enums.ContentType;
import com.aynikortex.backend.content.enums.FileFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ContentResponseDTO(
        UUID id,
        String title,
        ContentType contentType,
        String textContent,
        String fileName,
        FileFormat fileFormat,
        String category,
        String subcategory,
        BigDecimal confidence,
        List<KeywordDTO> keywords,
        String summary,
        LocalDateTime createdAt
) {}
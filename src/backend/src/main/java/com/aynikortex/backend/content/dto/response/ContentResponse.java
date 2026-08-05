package com.aynikortex.backend.content.dto.response;

import java.util.UUID;

public record ContentResponse(
        UUID id,
        String title,
        String summary,
        String category,
        String subcategory,
        Double confidence,
        List<KeywordResponse> keywords

) {}
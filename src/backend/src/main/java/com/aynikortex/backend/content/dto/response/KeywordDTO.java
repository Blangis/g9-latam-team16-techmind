package com.aynikortex.backend.content.dto.response;

import java.math.BigDecimal;

public record KeywordDTO(
        String term,
        BigDecimal score
) {
}

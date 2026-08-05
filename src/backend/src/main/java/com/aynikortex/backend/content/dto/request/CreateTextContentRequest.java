package com.aynikortex.backend.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTextContentRequest(

        @Size(max = 200)
        String title,

        @NotBlank
        String textContent

) {}
package com.codewitheli.joblens.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for analyzing a pasted job description. */
public record AnalyzeRequest(
        @NotBlank(message = "jobDescription must not be blank")
        String jobDescription
) {
}

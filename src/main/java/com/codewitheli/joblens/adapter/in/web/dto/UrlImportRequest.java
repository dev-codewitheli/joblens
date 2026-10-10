package com.codewitheli.joblens.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for importing a job posting from a URL. */
public record UrlImportRequest(
        @NotBlank(message = "Enter a URL.")
        String url
) {
}

package com.codewitheli.joblens.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for analyzing a pasted job description.
 *
 * @param jobDescription the posting text (required)
 * @param profile        optional, visitor-supplied skills profile; when omitted,
 *                       the server's sample profile is used
 */
public record AnalyzeRequest(
        @NotBlank(message = "jobDescription must not be blank")
        String jobDescription,

        @Valid
        ProfileInput profile
) {
}

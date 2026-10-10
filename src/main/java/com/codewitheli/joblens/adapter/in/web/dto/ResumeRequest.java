package com.codewitheli.joblens.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for extracting a profile from pasted résumé text. */
public record ResumeRequest(
        @NotBlank(message = "Paste your résumé text first.")
        String resumeText
) {
}

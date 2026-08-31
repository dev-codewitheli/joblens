package com.codewitheli.joblens.adapter.in.web.dto;

import com.codewitheli.joblens.domain.model.SkillProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * Request body for updating the skills-only profile. Deliberately has no fields
 * for name, email, or any other personal data.
 */
public record ProfileUpdateRequest(
        @NotBlank String headline,
        @PositiveOrZero int yearsOfExperience,
        List<String> skills,
        String experienceSummary,
        List<String> projectHighlights
) {
    /** Maps to a domain profile; the id is assigned by the repository. */
    public SkillProfile toDomain() {
        return new SkillProfile("demo", headline, yearsOfExperience, skills, experienceSummary, projectHighlights);
    }
}

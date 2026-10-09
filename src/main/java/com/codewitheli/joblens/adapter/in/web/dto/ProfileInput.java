package com.codewitheli.joblens.adapter.in.web.dto;

import com.codewitheli.joblens.domain.model.SkillProfile;

import java.util.List;

/**
 * An optional skills-only profile supplied by the visitor with an analyze request.
 *
 * <p>This is how JobLens stays useful to anyone without accounts or server-side
 * storage: the browser keeps the visitor's profile and sends it per request. It
 * is deliberately lenient — every field is optional — and, like the rest of the
 * app, holds no personal data (no name, email, or employer).
 */
public record ProfileInput(
        String headline,
        int yearsOfExperience,
        List<String> skills,
        String experienceSummary,
        List<String> projectHighlights
) {
    /** Maps to a domain profile, filling sensible defaults for blank fields. */
    public SkillProfile toDomain() {
        String safeHeadline = (headline == null || headline.isBlank()) ? "Professional" : headline.strip();
        return new SkillProfile(
                "custom",
                safeHeadline,
                Math.max(0, yearsOfExperience),
                skills,
                experienceSummary == null ? "" : experienceSummary,
                projectHighlights
        );
    }
}

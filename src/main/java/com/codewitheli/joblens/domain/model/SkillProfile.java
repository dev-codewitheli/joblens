package com.codewitheli.joblens.domain.model;

import java.util.List;

/**
 * A skills-only professional profile — deliberately free of personal data.
 *
 * <p>There are no fields for name, email, phone, or employer. JobLens only ever
 * needs the role-relevant parts to match against a job posting, so those are the
 * only parts it stores.
 *
 * @param id                 stable identifier for the profile
 * @param headline           short professional headline, e.g. "Senior Backend Engineer"
 * @param yearsOfExperience  total years of professional experience
 * @param skills             normalized skill/technology keywords
 * @param experienceSummary  a few sentences describing focus and strengths (no PII)
 * @param projectHighlights  short, anonymized project blurbs
 */
public record SkillProfile(
        String id,
        String headline,
        int yearsOfExperience,
        List<String> skills,
        String experienceSummary,
        List<String> projectHighlights
) {
    public SkillProfile {
        skills = skills == null ? List.of() : List.copyOf(skills);
        projectHighlights = projectHighlights == null ? List.of() : List.copyOf(projectHighlights);
    }
}

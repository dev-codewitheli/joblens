package com.codewitheli.joblens.domain.model;

import java.util.List;

/**
 * The result of analyzing a {@link JobPosting} against a {@link SkillProfile}.
 *
 * <p>Everything here is PII-free: generated bullets and the cover-letter draft use
 * placeholders like {@code [Your Name]} and {@code [Company]} that the user fills
 * in offline.
 *
 * @param matchScore       overall fit, 0–100
 * @param matched          requirements the profile satisfies
 * @param gaps             requirements the profile is missing
 * @param missingKeywords  ATS keywords worth adding to a resume
 * @param fitSummary       a short natural-language summary of the fit
 * @param suggestedBullets tailored resume bullet suggestions
 * @param coverLetterDraft a short, placeholder-based cover-letter draft
 */
public record JobAnalysis(
        int matchScore,
        List<String> matched,
        List<String> gaps,
        List<String> missingKeywords,
        String fitSummary,
        List<String> suggestedBullets,
        String coverLetterDraft
) {
    public JobAnalysis {
        matched = matched == null ? List.of() : List.copyOf(matched);
        gaps = gaps == null ? List.of() : List.copyOf(gaps);
        missingKeywords = missingKeywords == null ? List.of() : List.copyOf(missingKeywords);
        suggestedBullets = suggestedBullets == null ? List.of() : List.copyOf(suggestedBullets);
    }
}

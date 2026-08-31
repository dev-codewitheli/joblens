package com.codewitheli.joblens.domain.model;

/**
 * A job posting to analyze.
 *
 * @param title              best-effort role title (may be derived from the text)
 * @param companyPlaceholder a neutral placeholder; JobLens never requires a real
 *                           company name and uses "[Company]" by default
 * @param rawText            the pasted job-description text
 */
public record JobPosting(String title, String companyPlaceholder, String rawText) {

    public static JobPosting of(String rawText) {
        return new JobPosting("Role", "[Company]", rawText == null ? "" : rawText);
    }
}

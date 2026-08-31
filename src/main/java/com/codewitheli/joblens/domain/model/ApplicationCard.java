package com.codewitheli.joblens.domain.model;

import java.time.Instant;

/**
 * A tracked application on the Kanban board — a saved analysis plus its status.
 *
 * @param id                 stable identifier
 * @param jobTitle           the role title
 * @param companyPlaceholder neutral company placeholder (no real company required)
 * @param status             pipeline status
 * @param createdAt          when it was saved
 * @param analysis           the fit analysis captured when saved
 */
public record ApplicationCard(
        String id,
        String jobTitle,
        String companyPlaceholder,
        ApplicationStatus status,
        Instant createdAt,
        JobAnalysis analysis
) {
    /** Returns a copy of this card with a new status. */
    public ApplicationCard withStatus(ApplicationStatus newStatus) {
        return new ApplicationCard(id, jobTitle, companyPlaceholder, newStatus, createdAt, analysis);
    }
}

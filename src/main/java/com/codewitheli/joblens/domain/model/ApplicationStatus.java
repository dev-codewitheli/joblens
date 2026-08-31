package com.codewitheli.joblens.domain.model;

/**
 * Where an application sits in the job-search pipeline (the Kanban columns).
 */
public enum ApplicationStatus {
    SAVED,
    APPLIED,
    INTERVIEWING,
    OFFER,
    REJECTED
}

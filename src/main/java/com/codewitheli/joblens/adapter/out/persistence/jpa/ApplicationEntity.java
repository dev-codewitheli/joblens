package com.codewitheli.joblens.adapter.out.persistence.jpa;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA mapping for a tracked application. The structured {@link JobAnalysis} is
 * stored as JSON (see {@link JobAnalysisJsonConverter}).
 */
@Entity
@Table(name = "application_card")
public class ApplicationEntity {

    @Id
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "company_placeholder")
    private String companyPlaceholder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Convert(converter = JobAnalysisJsonConverter.class)
    @Column(name = "analysis", columnDefinition = "text")
    private JobAnalysis analysis;

    protected ApplicationEntity() {
        // for JPA
    }

    public static ApplicationEntity fromDomain(String clientId, ApplicationCard card) {
        ApplicationEntity e = new ApplicationEntity();
        e.id = card.id();
        e.clientId = clientId;
        e.jobTitle = card.jobTitle();
        e.companyPlaceholder = card.companyPlaceholder();
        e.status = card.status();
        e.createdAt = card.createdAt();
        e.analysis = card.analysis();
        return e;
    }

    public ApplicationCard toDomain() {
        return new ApplicationCard(id, jobTitle, companyPlaceholder, status, createdAt, analysis);
    }
}

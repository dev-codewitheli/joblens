package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.adapter.out.persistence.InMemoryApplicationRepository;
import com.codewitheli.joblens.adapter.out.persistence.InMemoryProfileRepository;
import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzeJobServiceTest {

    private final ApplicationRepository applications = new InMemoryApplicationRepository();

    // A stub analysis port so the service test is independent of any analyzer.
    private final JobAnalysisPort stubPort = (profile, posting) ->
            new JobAnalysis(80, List.of("Java"), List.of("Kafka"), List.of("Kafka"),
                    "Match 80%.", List.of("A bullet"), "Dear [Company], ...");

    private final InMemoryProfileRepository profiles = new InMemoryProfileRepository();

    private final AnalyzeJobService service =
            new AnalyzeJobService(stubPort, profiles, applications);

    @Test
    void analyzeReturnsAnalysisWithoutSaving() {
        JobAnalysis analysis = service.analyze("Java role");

        assertThat(analysis.matchScore()).isEqualTo(80);
        assertThat(applications.findAll()).isEmpty();
    }

    @Test
    void analyzeAndSavePersistsCardAsSaved() {
        ApplicationCard card = service.analyzeAndSave("""
                Backend Engineer
                Java and Spring Boot required.
                """);

        assertThat(card.status()).isEqualTo(ApplicationStatus.SAVED);
        assertThat(card.jobTitle()).isEqualTo("Backend Engineer");
        assertThat(card.analysis().matchScore()).isEqualTo(80);
        assertThat(applications.findAll()).hasSize(1);
    }

    @Test
    void usesSuppliedProfileOverSample() {
        // Capture which profile the analysis port actually receives.
        AtomicReference<SkillProfile> seen = new AtomicReference<>();
        JobAnalysisPort capturing = (profile, posting) -> {
            seen.set(profile);
            return new JobAnalysis(0, List.of(), List.of(), List.of(), "", List.of(), "");
        };
        AnalyzeJobService svc = new AnalyzeJobService(capturing, profiles, applications);

        SkillProfile custom = new SkillProfile("custom", "Frontend Engineer", 3,
                List.of("React", "TypeScript"), "", List.of());
        svc.analyze("Some role", custom);

        assertThat(seen.get()).isSameAs(custom);
    }

    @Test
    void fallsBackToSampleWhenProfileIsNull() {
        AtomicReference<SkillProfile> seen = new AtomicReference<>();
        JobAnalysisPort capturing = (profile, posting) -> {
            seen.set(profile);
            return new JobAnalysis(0, List.of(), List.of(), List.of(), "", List.of(), "");
        };
        AnalyzeJobService svc = new AnalyzeJobService(capturing, profiles, applications);

        svc.analyze("Some role", null);

        assertThat(seen.get()).isSameAs(profiles.getActive());
    }
}

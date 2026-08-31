package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.adapter.out.persistence.InMemoryApplicationRepository;
import com.codewitheli.joblens.adapter.out.persistence.InMemoryProfileRepository;
import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzeJobServiceTest {

    private final ApplicationRepository applications = new InMemoryApplicationRepository();

    // A stub analysis port so the service test is independent of any analyzer.
    private final JobAnalysisPort stubPort = (profile, posting) ->
            new JobAnalysis(80, List.of("Java"), List.of("Kafka"), List.of("Kafka"),
                    "Match 80%.", List.of("A bullet"), "Dear [Company], ...");

    private final AnalyzeJobService service =
            new AnalyzeJobService(stubPort, new InMemoryProfileRepository(), applications);

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
}

package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordJobAnalyzerTest {

    private final KeywordJobAnalyzer analyzer = new KeywordJobAnalyzer();

    private SkillProfile profile(String... skills) {
        return new SkillProfile("demo", "Senior Backend Engineer", 7, List.of(skills),
                "summary", List.of());
    }

    @Test
    void matchesRequiredSkillsAndFlagsGaps() {
        SkillProfile profile = profile("Java", "Spring Boot", "PostgreSQL", "RabbitMQ");
        JobPosting posting = JobPosting.of("""
                Senior Backend Engineer
                Requirements: Java, Spring Boot, Kafka, Kubernetes.
                """);

        JobAnalysis result = analyzer.analyze(profile, posting);

        assertThat(result.matched()).contains("Java", "Spring Boot");
        assertThat(result.gaps()).contains("Kafka", "Kubernetes");
        assertThat(result.matchScore()).isEqualTo(50); // 2 of 4 required
    }

    @Test
    void suggestsTransferableSkillForFamilyGap() {
        SkillProfile profile = profile("Java", "RabbitMQ");
        JobPosting posting = JobPosting.of("We use Kafka heavily for event streaming.");

        JobAnalysis result = analyzer.analyze(profile, posting);

        // RabbitMQ and Kafka share the "messaging" family → a bridging bullet.
        assertThat(result.suggestedBullets())
                .anyMatch(b -> b.contains("RabbitMQ") && b.contains("Kafka"));
    }

    @Test
    void wordBoundaryAvoidsFalsePositives() {
        // "javascript" must not be detected as "java".
        SkillProfile profile = profile("Java");
        JobPosting posting = JobPosting.of("Frontend role: javascript and typescript only.");

        JobAnalysis result = analyzer.analyze(profile, posting);

        assertThat(result.matched()).doesNotContain("Java");
    }
}

package com.codewitheli.joblens.adapter.out.persistence;

import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the PostgreSQL-backed board (JPA + Liquibase) end to end against a real
 * Postgres container. Skipped automatically when Docker isn't available.
 */
@SpringBootTest
@ActiveProfiles({"demo", "postgres"})
@Testcontainers(disabledWithoutDocker = true)
class JpaApplicationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    ApplicationRepository repository;

    private ApplicationCard card(String title) {
        JobAnalysis analysis = new JobAnalysis(72, List.of("Java"), List.of("Kafka"),
                List.of("Kafka"), "Match 72%.", List.of("A bullet"), "Dear [Company], ...");
        return new ApplicationCard(UUID.randomUUID().toString(), title, "[Company]",
                ApplicationStatus.SAVED, Instant.now(), analysis);
    }

    @Test
    void usesJpaAdapterUnderPostgresProfile() {
        assertThat(repository).isInstanceOf(JpaApplicationRepository.class);
    }

    @Test
    void savesFindsAndScopesByClient() {
        ApplicationCard saved = repository.save("alice", card("Backend Engineer"));

        assertThat(repository.findAll("alice")).hasSize(1);
        assertThat(repository.findAll("bob")).isEmpty();                 // scoped per client
        assertThat(repository.findById("alice", saved.id())).isPresent();
        assertThat(repository.findById("bob", saved.id())).isEmpty();

        // round-trip preserves the JSON-stored analysis
        assertThat(repository.findById("alice", saved.id()).get().analysis().matchScore()).isEqualTo(72);
    }

    @Test
    void updatesStatusAndDeletes() {
        ApplicationCard saved = repository.save("carol", card("Platform Engineer"));

        repository.save("carol", saved.withStatus(ApplicationStatus.APPLIED));
        assertThat(repository.findById("carol", saved.id()).get().status())
                .isEqualTo(ApplicationStatus.APPLIED);

        assertThat(repository.deleteById("carol", saved.id())).isTrue();
        assertThat(repository.findById("carol", saved.id())).isEmpty();
        assertThat(repository.deleteById("carol", saved.id())).isFalse();
    }
}

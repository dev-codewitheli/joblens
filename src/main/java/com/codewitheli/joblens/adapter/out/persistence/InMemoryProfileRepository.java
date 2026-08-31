package com.codewitheli.joblens.adapter.out.persistence;

import com.codewitheli.joblens.application.port.out.ProfileRepository;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory profile store seeded with a fictional demo profile.
 *
 * <p>MVP persistence: data resets on restart. A PostgreSQL/JPA adapter is the
 * next milestone (see the roadmap in the README).
 */
@Repository
public class InMemoryProfileRepository implements ProfileRepository {

    private final AtomicReference<SkillProfile> active = new AtomicReference<>(demoProfile());

    @Override
    public SkillProfile getActive() {
        return active.get();
    }

    @Override
    public SkillProfile save(SkillProfile profile) {
        // Preserve the stable id; only the content changes.
        SkillProfile current = active.get();
        SkillProfile updated = new SkillProfile(
                current.id(),
                profile.headline(),
                profile.yearsOfExperience(),
                profile.skills(),
                profile.experienceSummary(),
                profile.projectHighlights()
        );
        active.set(updated);
        return updated;
    }

    /** A fictional, PII-free profile so the demo works out of the box. */
    private static SkillProfile demoProfile() {
        return new SkillProfile(
                "demo",
                "Senior Backend Engineer",
                7,
                List.of(
                        "Java", "Spring Boot", "Spring Security", "Spring Data JPA",
                        "REST APIs", "PostgreSQL", "Hexagonal Architecture",
                        "Docker", "Microservices", "RabbitMQ", "JWT", "TDD", "Spring AI"
                ),
                "Backend engineer focused on well-bounded, event-driven services on the JVM. "
                        + "Comfortable owning features end to end — API design, data modeling, testing, "
                        + "containerization, and production support.",
                List.of(
                        "Built an appointment-scheduling platform with Spring Boot, PostgreSQL and RabbitMQ.",
                        "Designed a public services portal handling payments, documents and notifications.",
                        "Automated report generation and email workflows for an insurance system."
                )
        );
    }
}

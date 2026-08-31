package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Deterministic, offline analysis port — the default "demo" analyzer.
 *
 * <p>It recognizes a dictionary of technologies (with aliases), detects which are
 * required by the posting, matches them against the profile, and flags
 * transferable skills (e.g. RabbitMQ ↔ Kafka via the "messaging" family). It runs
 * with no API key and no network, which makes the public demo free and safe. The
 * Spring AI analyzer ({@code ai} profile) is a drop-in replacement.
 */
@Component
@Profile("demo")
public class KeywordJobAnalyzer implements JobAnalysisPort {

    /** alias (lowercase) -> canonical display name. */
    private static final Map<String, String> ALIASES = new LinkedHashMap<>();
    /** canonical -> family, for transferable-skill hints. */
    private static final Map<String, String> FAMILY = new LinkedHashMap<>();

    static {
        register("Java", "java");
        register("Spring Boot", "spring boot", "springboot");
        register("Spring Security", "spring security");
        register("Spring Data JPA", "spring data jpa", "jpa", "hibernate");
        register("Spring AI", "spring ai");
        register("REST APIs", "rest apis", "rest api", "restful", "rest");
        register("GraphQL", "graphql");
        register("PostgreSQL", "postgresql", "postgres", "postgre");
        register("MySQL", "mysql");
        register("MongoDB", "mongodb", "mongo");
        register("Redis", "redis");
        register("Hexagonal Architecture", "hexagonal architecture", "hexagonal", "ports and adapters");
        register("Microservices", "microservices", "microservice");
        register("Docker", "docker");
        register("Kubernetes", "kubernetes", "k8s");
        register("CI/CD", "ci/cd", "cicd", "ci cd");
        register("JWT", "jwt");
        register("TDD", "tdd", "test-driven", "test driven");
        register("AWS", "aws", "amazon web services");
        register("GCP", "gcp", "google cloud");
        register("Azure", "azure");
        register("Kafka", "kafka");
        register("RabbitMQ", "rabbitmq", "rabbit mq", "amqp");
        register("Angular", "angular");
        register("React", "react");
        register("TypeScript", "typescript");

        // Transferable families: skills in the same family bridge to one another.
        family("messaging", "Kafka", "RabbitMQ");
        family("cloud", "AWS", "GCP", "Azure");
        family("relational-db", "PostgreSQL", "MySQL");
        family("spa", "Angular", "React");
    }

    private static void register(String canonical, String... aliases) {
        ALIASES.put(canonical.toLowerCase(), canonical);
        for (String alias : aliases) {
            ALIASES.put(alias.toLowerCase(), canonical);
        }
    }

    private static void family(String family, String... canonicals) {
        for (String c : canonicals) {
            FAMILY.put(c, family);
        }
    }

    @Override
    public JobAnalysis analyze(SkillProfile profile, JobPosting posting) {
        String text = posting.rawText() == null ? "" : posting.rawText().toLowerCase();

        Set<String> required = detect(text);
        Set<String> owned = profileSkills(profile);

        List<String> matched = new ArrayList<>();
        List<String> gaps = new ArrayList<>();
        for (String req : required) {
            if (owned.contains(req)) {
                matched.add(req);
            } else {
                gaps.add(req);
            }
        }

        int score = required.isEmpty()
                ? (owned.isEmpty() ? 0 : 50)
                : Math.round(100f * matched.size() / required.size());

        String fitSummary = buildFitSummary(score, matched, gaps, profile);
        List<String> bullets = buildBullets(matched, gaps, owned, profile);
        String coverLetter = buildCoverLetter(posting, matched, profile);

        return new JobAnalysis(score, matched, gaps, List.copyOf(gaps), fitSummary, bullets, coverLetter);
    }

    /** Canonical technologies mentioned in the posting, in first-seen order. */
    private Set<String> detect(String text) {
        Set<String> found = new LinkedHashSet<>();
        for (Map.Entry<String, String> e : ALIASES.entrySet()) {
            if (containsWord(text, e.getKey())) {
                found.add(e.getValue());
            }
        }
        return found;
    }

    private Set<String> profileSkills(SkillProfile profile) {
        Set<String> owned = new LinkedHashSet<>();
        for (String skill : profile.skills()) {
            owned.add(ALIASES.getOrDefault(skill.toLowerCase(), skill));
        }
        return owned;
    }

    private static boolean containsWord(String haystack, String needle) {
        String regex = "\\b" + Pattern.quote(needle) + "\\b";
        return Pattern.compile(regex).matcher(haystack).find();
    }

    /** A transferable skill the profile owns in the same family as a gap, if any. */
    private String transferableFor(String gap, Set<String> owned) {
        String family = FAMILY.get(gap);
        if (family == null) {
            return null;
        }
        return owned.stream()
                .filter(s -> family.equals(FAMILY.get(s)))
                .findFirst()
                .orElse(null);
    }

    private String buildFitSummary(int score, List<String> matched, List<String> gaps, SkillProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Match ").append(score).append("%. ");
        if (!matched.isEmpty()) {
            sb.append("Strong on ").append(String.join(", ", matched)).append(". ");
        }
        if (gaps.isEmpty()) {
            sb.append("No obvious gaps against the listed requirements.");
        } else {
            sb.append("Gaps to address: ").append(String.join(", ", gaps)).append(".");
        }
        return sb.toString();
    }

    private List<String> buildBullets(List<String> matched, List<String> gaps,
                                      Set<String> owned, SkillProfile profile) {
        List<String> bullets = new ArrayList<>();
        if (!matched.isEmpty()) {
            String top = String.join(", ", matched.subList(0, Math.min(3, matched.size())));
            bullets.add("Delivered production features using " + top
                    + ", owning design, testing and deployment end to end.");
        }
        if (matched.contains("Microservices") || matched.contains("REST APIs")) {
            bullets.add("Designed and documented REST APIs for service-to-service communication, "
                    + "with a focus on clear contracts and backward compatibility.");
        }
        for (String gap : gaps) {
            String bridge = transferableFor(gap, owned);
            if (bridge != null) {
                bullets.add("Hands-on with " + bridge + " (same " + FAMILY.get(gap)
                        + " space as " + gap + ") — a fast ramp to " + gap + ".");
            }
        }
        if (bullets.isEmpty()) {
            bullets.add("Adaptable engineer comfortable picking up new tools quickly; "
                    + profile.yearsOfExperience() + "+ years shipping backend software.");
        }
        return bullets;
    }

    private String buildCoverLetter(JobPosting posting, List<String> matched, SkillProfile profile) {
        String strengths = matched.isEmpty()
                ? "backend engineering"
                : String.join(", ", matched.subList(0, Math.min(3, matched.size())));
        return """
                Dear Hiring Team at %s,

                I'm excited to apply for the %s role. As a %s with %d+ years of experience,
                I bring hands-on depth in %s and a track record of owning features end to end.

                I'd welcome the chance to discuss how I can contribute to your team.

                Best regards,
                [Your Name]
                """.formatted(
                posting.companyPlaceholder(),
                posting.title(),
                profile.headline(),
                profile.yearsOfExperience(),
                strengths
        );
    }
}

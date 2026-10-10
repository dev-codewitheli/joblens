package com.codewitheli.joblens.application.skills;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A small, shared dictionary of technologies with aliases and "transferable"
 * families. Powers keyword matching (demo analyzer), résumé skill extraction, and
 * the skills autocomplete — one source of truth for recognized skills.
 */
@Component
public class TechDictionary {

    /** alias (lowercase) -> canonical display name. */
    private final Map<String, String> aliases = new LinkedHashMap<>();
    /** canonical -> family, for transferable-skill hints. */
    private final Map<String, String> family = new LinkedHashMap<>();
    /** canonical display names, in insertion order. */
    private final List<String> canonicals = new ArrayList<>();
    /** cached compiled word-boundary patterns per alias. */
    private final Map<String, Pattern> patterns = new LinkedHashMap<>();

    public TechDictionary() {
        register("Java", "java");
        register("Kotlin", "kotlin");
        register("Spring Boot", "spring boot", "springboot");
        register("Spring Framework", "spring framework", "spring mvc", "spring core");
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
        register("OAuth2", "oauth2", "oauth 2", "oauth");
        register("TDD", "tdd", "test-driven", "test driven");
        register("AWS", "aws", "amazon web services");
        register("GCP", "gcp", "google cloud");
        register("Azure", "azure");
        register("Kafka", "kafka");
        register("RabbitMQ", "rabbitmq", "rabbit mq", "amqp");
        register("Gradle", "gradle");
        register("Maven", "maven");
        register("Git", "git");
        register("Angular", "angular");
        register("React", "react");
        register("TypeScript", "typescript");
        register("JavaScript", "javascript");
        register("Flutter", "flutter");

        family("messaging", "Kafka", "RabbitMQ");
        family("cloud", "AWS", "GCP", "Azure");
        family("relational-db", "PostgreSQL", "MySQL");
        family("spa", "Angular", "React");
    }

    private void register(String canonical, String... aliasList) {
        canonicals.add(canonical);
        addAlias(canonical.toLowerCase(Locale.ROOT), canonical);
        for (String alias : aliasList) {
            addAlias(alias.toLowerCase(Locale.ROOT), canonical);
        }
    }

    private void addAlias(String alias, String canonical) {
        aliases.put(alias, canonical);
        patterns.put(alias, Pattern.compile("\\b" + Pattern.quote(alias) + "\\b"));
    }

    private void family(String name, String... canonicalNames) {
        for (String c : canonicalNames) {
            family.put(c, name);
        }
    }

    /** Canonical technologies mentioned in the text, in dictionary order. */
    public Set<String> detect(String text) {
        String haystack = text == null ? "" : text.toLowerCase(Locale.ROOT);
        Set<String> found = new LinkedHashSet<>();
        for (Map.Entry<String, String> e : aliases.entrySet()) {
            if (patterns.get(e.getKey()).matcher(haystack).find()) {
                found.add(e.getValue());
            }
        }
        return found;
    }

    /** Maps a free-form skill to its canonical name (or returns it unchanged). */
    public String canonical(String skill) {
        if (skill == null) {
            return "";
        }
        return aliases.getOrDefault(skill.toLowerCase(Locale.ROOT).trim(), skill.trim());
    }

    public Set<String> canonicalize(Collection<String> skills) {
        Set<String> out = new LinkedHashSet<>();
        if (skills != null) {
            for (String s : skills) {
                out.add(canonical(s));
            }
        }
        return out;
    }

    public String familyOf(String canonical) {
        return family.get(canonical);
    }

    /** Autocomplete: canonical names matching the prefix/substring, capped. */
    public List<String> suggest(String query, int limit) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        List<String> starts = new ArrayList<>();
        List<String> contains = new ArrayList<>();
        for (String c : canonicals) {
            String lc = c.toLowerCase(Locale.ROOT);
            if (q.isEmpty() || lc.startsWith(q)) {
                starts.add(c);
            } else if (lc.contains(q)) {
                contains.add(c);
            }
        }
        starts.addAll(contains);
        return starts.size() > limit ? starts.subList(0, limit) : starts;
    }
}

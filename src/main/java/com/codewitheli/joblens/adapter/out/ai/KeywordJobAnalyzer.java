package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.application.skills.TechDictionary;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Deterministic, offline analysis port — the default "demo" analyzer.
 *
 * <p>Uses the shared {@link TechDictionary} to detect technologies required by the
 * posting, match them against the profile, and flag transferable skills (e.g.
 * RabbitMQ ↔ Kafka via the "messaging" family). It runs with no API key and no
 * network. The Spring AI analyzer ({@code ai} profile) is a drop-in replacement.
 */
@Component
@Profile("demo")
public class KeywordJobAnalyzer implements JobAnalysisPort {

    private final TechDictionary dictionary;

    public KeywordJobAnalyzer(TechDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public JobAnalysis analyze(SkillProfile profile, JobPosting posting) {
        Set<String> required = dictionary.detect(posting.rawText());
        Set<String> owned = dictionary.canonicalize(profile.skills());

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

        String fitSummary = buildFitSummary(score, matched, gaps);
        List<String> bullets = buildBullets(matched, gaps, owned, profile);
        String coverLetter = buildCoverLetter(posting, matched, profile);

        return new JobAnalysis(score, matched, gaps, List.copyOf(gaps), fitSummary, bullets, coverLetter);
    }

    /** A transferable skill the profile owns in the same family as a gap, if any. */
    private String transferableFor(String gap, Set<String> owned) {
        String fam = dictionary.familyOf(gap);
        if (fam == null) {
            return null;
        }
        return owned.stream()
                .filter(s -> fam.equals(dictionary.familyOf(s)))
                .findFirst()
                .orElse(null);
    }

    private String buildFitSummary(int score, List<String> matched, List<String> gaps) {
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
                bullets.add("Hands-on with " + bridge + " (same " + dictionary.familyOf(gap)
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

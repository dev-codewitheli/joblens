package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Spring AI–backed analysis port (active under the {@code ai} profile).
 *
 * <p>Uses a {@link ChatClient} to extract requirements, reason about fit, and
 * generate PII-free suggestions, returning a structured {@link JobAnalysis} via
 * Spring AI's entity mapping. The provider (OpenAI, Groq, local Ollama, …) is
 * chosen entirely through configuration — this code is provider-agnostic.
 */
@Component
@Profile("ai")
public class SpringAiJobAnalyzer implements JobAnalysisPort {

    private static final String SYSTEM = """
            You are a precise technical recruiter assisting a job applicant.
            Analyze how well a candidate profile fits a job description.
            Rules:
            - Be accurate and concise; do not invent skills the candidate lacks.
            - matchScore is an integer 0-100 reflecting fit against the stated requirements.
            - matched = requirements the profile clearly satisfies.
            - gaps = requirements the profile is missing.
            - missingKeywords = ATS keywords from the posting worth adding to a resume.
            - suggestedBullets = tailored, truthful resume bullet suggestions.
            - coverLetterDraft = a short draft that MUST use the placeholders
              [Your Name] and [Company]; never invent a real name, email, or company.
            Return only the requested structured data.
            """;

    private static final String USER = """
            CANDIDATE PROFILE (skills only, no personal data):
            Headline: {headline}
            Years of experience: {years}
            Skills: {skills}
            Summary: {summary}

            JOB DESCRIPTION:
            {jd}
            """;

    private final ChatClient chat;

    public SpringAiJobAnalyzer(ChatClient.Builder builder) {
        this.chat = builder.defaultSystem(SYSTEM).build();
    }

    @Override
    public JobAnalysis analyze(SkillProfile profile, JobPosting posting) {
        return chat.prompt()
                .user(u -> u.text(USER)
                        .param("headline", profile.headline())
                        .param("years", String.valueOf(profile.yearsOfExperience()))
                        .param("skills", String.join(", ", profile.skills()))
                        .param("summary", profile.experienceSummary() == null ? "" : profile.experienceSummary())
                        .param("jd", posting.rawText()))
                .call()
                .entity(JobAnalysis.class);
    }
}

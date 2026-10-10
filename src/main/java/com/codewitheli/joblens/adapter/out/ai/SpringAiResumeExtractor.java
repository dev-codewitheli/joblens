package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.port.out.ResumeExtractorPort;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * AI résumé extractor (active under the {@code ai} profile): uses the LLM to pull a
 * headline, years of experience, skills, and a short summary — all PII-free.
 */
@Component
@Profile("ai")
public class SpringAiResumeExtractor implements ResumeExtractorPort {

    private static final String SYSTEM = """
            Extract a skills-only professional profile from the résumé text.
            Rules:
            - Return ONLY role-relevant content; never include name, email, phone,
              address, or employer names (no personal data).
            - skills: concise, de-duplicated technology/skill keywords.
            - headline: a short professional title (e.g. "Senior Backend Engineer").
            - yearsOfExperience: integer estimate from the text (0 if unclear).
            - experienceSummary: 1-2 neutral sentences, no PII.
            Use the id value "extracted".
            """;

    private static final String USER = "RÉSUMÉ:\n{resume}";

    private final ChatClient chat;

    public SpringAiResumeExtractor(ChatClient.Builder builder) {
        this.chat = builder.defaultSystem(SYSTEM).build();
    }

    @Override
    public SkillProfile extract(String resumeText) {
        return chat.prompt()
                .user(u -> u.text(USER).param("resume", resumeText))
                .call()
                .entity(SkillProfile.class);
    }
}

package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Outbound port: derive a skills-only profile from pasted résumé text.
 *
 * <p>The demo adapter extracts recognized skills with the {@link
 * com.codewitheli.joblens.application.skills.TechDictionary}; the AI adapter uses
 * the LLM for a richer extraction. Nothing is stored.
 */
public interface ResumeExtractorPort {

    SkillProfile extract(String resumeText);
}

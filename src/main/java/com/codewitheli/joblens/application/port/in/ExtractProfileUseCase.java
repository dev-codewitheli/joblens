package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Inbound port: extract a skills-only profile from pasted résumé text so the
 * visitor can autofill their profile. The result is returned, never stored.
 */
public interface ExtractProfileUseCase {

    SkillProfile fromResume(String resumeText);
}

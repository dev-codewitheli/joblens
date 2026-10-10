package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.application.BadInputException;
import com.codewitheli.joblens.application.port.in.ExtractProfileUseCase;
import com.codewitheli.joblens.application.port.out.ResumeExtractorPort;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.stereotype.Service;

/**
 * Extracts a skills-only profile from résumé text via the active extractor.
 */
@Service
public class ExtractProfileService implements ExtractProfileUseCase {

    private final ResumeExtractorPort extractor;

    public ExtractProfileService(ResumeExtractorPort extractor) {
        this.extractor = extractor;
    }

    @Override
    public SkillProfile fromResume(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            throw new BadInputException("Paste your résumé text first.");
        }
        return extractor.extract(resumeText);
    }
}

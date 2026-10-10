package com.codewitheli.joblens.adapter.out.ai;

import com.codewitheli.joblens.application.port.out.ResumeExtractorPort;
import com.codewitheli.joblens.application.skills.TechDictionary;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo résumé extractor: pulls recognized skills from the text using the shared
 * dictionary. Deterministic, offline — no headline/years inference (the AI adapter
 * does that).
 */
@Component
@Profile("demo")
public class KeywordResumeExtractor implements ResumeExtractorPort {

    private final TechDictionary dictionary;

    public KeywordResumeExtractor(TechDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public SkillProfile extract(String resumeText) {
        List<String> skills = new ArrayList<>(dictionary.detect(resumeText));
        return new SkillProfile("extracted", "", 0, skills, "", List.of());
    }
}

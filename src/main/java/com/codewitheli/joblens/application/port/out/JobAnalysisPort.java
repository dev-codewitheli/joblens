package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Outbound port for producing a {@link JobAnalysis} from a posting and a profile.
 *
 * <p>Two adapters implement this: a deterministic, offline keyword analyzer (the
 * default "demo" mode) and a Spring AI–backed analyzer (the "ai" profile). The
 * application core neither knows nor cares which one is wired in.
 */
public interface JobAnalysisPort {

    JobAnalysis analyze(SkillProfile profile, JobPosting posting);
}

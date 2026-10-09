package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Inbound port: analyze a job description against a profile.
 *
 * <p>Each operation has a variant that takes a visitor-supplied profile. When the
 * profile is {@code null}, the server's sample profile is used instead.
 */
public interface AnalyzeJobUseCase {

    /** Analyze against the sample profile, without saving. */
    JobAnalysis analyze(String jobDescription);

    /** Analyze against a supplied profile (or the sample when {@code null}), without saving. */
    JobAnalysis analyze(String jobDescription, SkillProfile profile);

    /** Analyze against the sample profile and save it to the board as SAVED. */
    ApplicationCard analyzeAndSave(String jobDescription);

    /** Analyze against a supplied profile (or the sample when {@code null}) and save it. */
    ApplicationCard analyzeAndSave(String jobDescription, SkillProfile profile);
}

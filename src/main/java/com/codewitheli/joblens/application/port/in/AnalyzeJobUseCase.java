package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.SkillProfile;

/**
 * Inbound port: analyze a job description against a profile.
 *
 * <p>Each analyze variant takes a visitor-supplied profile; when {@code null}, the
 * server's sample profile is used. Saving to the board is scoped by client id.
 */
public interface AnalyzeJobUseCase {

    /** Analyze against the sample profile, without saving. */
    JobAnalysis analyze(String jobDescription);

    /** Analyze against a supplied profile (or the sample when {@code null}), without saving. */
    JobAnalysis analyze(String jobDescription, SkillProfile profile);

    /** Analyze against a supplied profile and save it to the client's board as SAVED. */
    ApplicationCard analyzeAndSave(String clientId, String jobDescription, SkillProfile profile);
}

package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.JobAnalysis;

/**
 * Inbound port: analyze a job description against the active profile.
 */
public interface AnalyzeJobUseCase {

    /** Analyze a pasted job description without saving it. */
    JobAnalysis analyze(String jobDescription);

    /** Analyze a pasted job description and save it to the board as SAVED. */
    ApplicationCard analyzeAndSave(String jobDescription);
}

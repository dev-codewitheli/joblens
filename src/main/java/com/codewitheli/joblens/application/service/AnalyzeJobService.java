package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.application.port.in.AnalyzeJobUseCase;
import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.application.port.out.JobAnalysisPort;
import com.codewitheli.joblens.application.port.out.ProfileRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import com.codewitheli.joblens.domain.model.JobPosting;
import com.codewitheli.joblens.domain.model.SkillProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Orchestrates a job analysis: resolve the profile (visitor-supplied or the
 * sample), delegate to the analysis port (keyword or AI), and optionally persist
 * the result as a board card.
 */
@Service
public class AnalyzeJobService implements AnalyzeJobUseCase {

    private final JobAnalysisPort analysisPort;
    private final ProfileRepository profiles;
    private final ApplicationRepository applications;

    public AnalyzeJobService(JobAnalysisPort analysisPort,
                             ProfileRepository profiles,
                             ApplicationRepository applications) {
        this.analysisPort = analysisPort;
        this.profiles = profiles;
        this.applications = applications;
    }

    @Override
    public JobAnalysis analyze(String jobDescription) {
        return analyze(jobDescription, null);
    }

    @Override
    public JobAnalysis analyze(String jobDescription, SkillProfile profile) {
        JobPosting posting = toPosting(jobDescription);
        return analysisPort.analyze(resolve(profile), posting);
    }

    @Override
    public ApplicationCard analyzeAndSave(String jobDescription) {
        return analyzeAndSave(jobDescription, null);
    }

    @Override
    public ApplicationCard analyzeAndSave(String jobDescription, SkillProfile profile) {
        JobPosting posting = toPosting(jobDescription);
        JobAnalysis analysis = analysisPort.analyze(resolve(profile), posting);

        ApplicationCard card = new ApplicationCard(
                UUID.randomUUID().toString(),
                posting.title(),
                posting.companyPlaceholder(),
                ApplicationStatus.SAVED,
                Instant.now(),
                analysis
        );
        return applications.save(card);
    }

    /** Use the visitor's profile when supplied; otherwise fall back to the sample. */
    private SkillProfile resolve(SkillProfile profile) {
        return profile != null ? profile : profiles.getActive();
    }

    /** Builds a posting from raw text, deriving a best-effort title from the first line. */
    private JobPosting toPosting(String jobDescription) {
        String text = jobDescription == null ? "" : jobDescription.strip();
        String title = deriveTitle(text);
        return new JobPosting(title, "[Company]", text);
    }

    private String deriveTitle(String text) {
        if (text.isBlank()) {
            return "Untitled role";
        }
        String firstLine = text.lines()
                .map(String::strip)
                .filter(line -> !line.isBlank())
                .findFirst()
                .orElse("Untitled role");
        return firstLine.length() > 80 ? firstLine.substring(0, 80).strip() + "…" : firstLine;
    }
}

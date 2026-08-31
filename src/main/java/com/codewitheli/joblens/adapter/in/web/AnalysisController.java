package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.adapter.in.web.dto.AnalyzeRequest;
import com.codewitheli.joblens.application.port.in.AnalyzeJobUseCase;
import com.codewitheli.joblens.domain.model.JobAnalysis;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Analyze a job description without saving it. */
@RestController
@RequestMapping("/api/analyze")
public class AnalysisController {

    private final AnalyzeJobUseCase analyzeJob;

    public AnalysisController(AnalyzeJobUseCase analyzeJob) {
        this.analyzeJob = analyzeJob;
    }

    @PostMapping
    public JobAnalysis analyze(@Valid @RequestBody AnalyzeRequest request) {
        return analyzeJob.analyze(request.jobDescription());
    }
}

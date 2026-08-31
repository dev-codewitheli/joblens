package com.codewitheli.joblens.adapter.in.web;

import com.codewitheli.joblens.adapter.in.web.dto.AnalyzeRequest;
import com.codewitheli.joblens.adapter.in.web.dto.UpdateStatusRequest;
import com.codewitheli.joblens.application.port.in.AnalyzeJobUseCase;
import com.codewitheli.joblens.application.port.in.TrackApplicationsUseCase;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** The Kanban board: analyze-and-save, list, move, and delete applications. */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final AnalyzeJobUseCase analyzeJob;
    private final TrackApplicationsUseCase track;

    public ApplicationController(AnalyzeJobUseCase analyzeJob, TrackApplicationsUseCase track) {
        this.analyzeJob = analyzeJob;
        this.track = track;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationCard create(@Valid @RequestBody AnalyzeRequest request) {
        return analyzeJob.analyzeAndSave(request.jobDescription());
    }

    @GetMapping
    public List<ApplicationCard> list() {
        return track.list();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationCard> updateStatus(@PathVariable String id,
                                                        @Valid @RequestBody UpdateStatusRequest request) {
        return track.updateStatus(id, request.status())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return track.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

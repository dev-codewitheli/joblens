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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The Kanban board, scoped per visitor via the {@code X-Client-Id} header (a random
 * id the browser generates). analyze-and-save, list, move, and delete.
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private static final String CLIENT_HEADER = "X-Client-Id";
    private static final String ANONYMOUS = "anonymous";

    private final AnalyzeJobUseCase analyzeJob;
    private final TrackApplicationsUseCase track;

    public ApplicationController(AnalyzeJobUseCase analyzeJob, TrackApplicationsUseCase track) {
        this.analyzeJob = analyzeJob;
        this.track = track;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationCard create(@RequestHeader(value = CLIENT_HEADER, required = false) String clientId,
                                  @Valid @RequestBody AnalyzeRequest request) {
        var profile = request.profile() == null ? null : request.profile().toDomain();
        return analyzeJob.analyzeAndSave(clientId(clientId), request.jobDescription(), profile);
    }

    @GetMapping
    public List<ApplicationCard> list(@RequestHeader(value = CLIENT_HEADER, required = false) String clientId) {
        return track.list(clientId(clientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationCard> updateStatus(
            @RequestHeader(value = CLIENT_HEADER, required = false) String clientId,
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return track.updateStatus(clientId(clientId), id, request.status())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader(value = CLIENT_HEADER, required = false) String clientId,
                                       @PathVariable String id) {
        return track.delete(clientId(clientId), id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private String clientId(String header) {
        return (header == null || header.isBlank()) ? ANONYMOUS : header.trim();
    }
}

package com.codewitheli.joblens.application.service;

import com.codewitheli.joblens.application.port.in.TrackApplicationsUseCase;
import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Manages the tracked applications on the Kanban board.
 */
@Service
public class ApplicationService implements TrackApplicationsUseCase {

    private final ApplicationRepository applications;

    public ApplicationService(ApplicationRepository applications) {
        this.applications = applications;
    }

    @Override
    public List<ApplicationCard> list() {
        return applications.findAll().stream()
                .sorted(Comparator.comparing(ApplicationCard::createdAt).reversed())
                .toList();
    }

    @Override
    public Optional<ApplicationCard> updateStatus(String id, ApplicationStatus status) {
        return applications.findById(id)
                .map(card -> applications.save(card.withStatus(status)));
    }

    @Override
    public boolean delete(String id) {
        return applications.deleteById(id);
    }
}

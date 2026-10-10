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
 * Manages the tracked applications on a visitor's Kanban board (scoped by client id).
 */
@Service
public class ApplicationService implements TrackApplicationsUseCase {

    private final ApplicationRepository applications;

    public ApplicationService(ApplicationRepository applications) {
        this.applications = applications;
    }

    @Override
    public List<ApplicationCard> list(String clientId) {
        return applications.findAll(clientId).stream()
                .sorted(Comparator.comparing(ApplicationCard::createdAt).reversed())
                .toList();
    }

    @Override
    public Optional<ApplicationCard> updateStatus(String clientId, String id, ApplicationStatus status) {
        return applications.findById(clientId, id)
                .map(card -> applications.save(clientId, card.withStatus(status)));
    }

    @Override
    public boolean delete(String clientId, String id) {
        return applications.deleteById(clientId, id);
    }
}

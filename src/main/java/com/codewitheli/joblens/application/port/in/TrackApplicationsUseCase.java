package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port: manage the tracked applications on a visitor's Kanban board,
 * scoped by their anonymous client id.
 */
public interface TrackApplicationsUseCase {

    List<ApplicationCard> list(String clientId);

    Optional<ApplicationCard> updateStatus(String clientId, String id, ApplicationStatus status);

    boolean delete(String clientId, String id);
}

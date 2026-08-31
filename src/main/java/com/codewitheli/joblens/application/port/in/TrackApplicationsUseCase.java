package com.codewitheli.joblens.application.port.in;

import com.codewitheli.joblens.domain.model.ApplicationCard;
import com.codewitheli.joblens.domain.model.ApplicationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Inbound port: manage the tracked applications on the Kanban board.
 */
public interface TrackApplicationsUseCase {

    List<ApplicationCard> list();

    Optional<ApplicationCard> updateStatus(String id, ApplicationStatus status);

    boolean delete(String id);
}

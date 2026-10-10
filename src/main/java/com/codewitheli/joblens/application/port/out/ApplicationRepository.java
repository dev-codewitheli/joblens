package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.ApplicationCard;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting tracked applications (the Kanban board).
 *
 * <p>Scoped by an anonymous {@code clientId} (the browser's random id) so each
 * visitor sees only their own cards — no accounts required.
 */
public interface ApplicationRepository {

    ApplicationCard save(String clientId, ApplicationCard card);

    List<ApplicationCard> findAll(String clientId);

    Optional<ApplicationCard> findById(String clientId, String id);

    boolean deleteById(String clientId, String id);
}

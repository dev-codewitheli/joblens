package com.codewitheli.joblens.application.port.out;

import com.codewitheli.joblens.domain.model.ApplicationCard;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting tracked applications (the Kanban board).
 */
public interface ApplicationRepository {

    ApplicationCard save(ApplicationCard card);

    List<ApplicationCard> findAll();

    Optional<ApplicationCard> findById(String id);

    boolean deleteById(String id);
}

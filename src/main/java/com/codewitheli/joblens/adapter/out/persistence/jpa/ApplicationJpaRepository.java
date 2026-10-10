package com.codewitheli.joblens.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Spring Data repository for {@link ApplicationEntity}, scoped by client id. */
public interface ApplicationJpaRepository extends JpaRepository<ApplicationEntity, String> {

    List<ApplicationEntity> findByClientId(String clientId);

    Optional<ApplicationEntity> findByClientIdAndId(String clientId, String id);
}

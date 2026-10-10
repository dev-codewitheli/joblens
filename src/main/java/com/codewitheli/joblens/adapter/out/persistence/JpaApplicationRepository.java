package com.codewitheli.joblens.adapter.out.persistence;

import com.codewitheli.joblens.adapter.out.persistence.jpa.ApplicationEntity;
import com.codewitheli.joblens.adapter.out.persistence.jpa.ApplicationJpaRepository;
import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL-backed board store (active under the {@code postgres} profile), scoped
 * per client id. Replaces the in-memory adapter when persistence is enabled.
 */
@Repository
@Profile("postgres")
public class JpaApplicationRepository implements ApplicationRepository {

    private final ApplicationJpaRepository jpa;

    public JpaApplicationRepository(ApplicationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public ApplicationCard save(String clientId, ApplicationCard card) {
        jpa.save(ApplicationEntity.fromDomain(clientId, card));
        return card;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationCard> findAll(String clientId) {
        return jpa.findByClientId(clientId).stream()
                .map(ApplicationEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationCard> findById(String clientId, String id) {
        return jpa.findByClientIdAndId(clientId, id).map(ApplicationEntity::toDomain);
    }

    @Override
    @Transactional
    public boolean deleteById(String clientId, String id) {
        return jpa.findByClientIdAndId(clientId, id).map(entity -> {
            jpa.delete(entity);
            return true;
        }).orElse(false);
    }
}

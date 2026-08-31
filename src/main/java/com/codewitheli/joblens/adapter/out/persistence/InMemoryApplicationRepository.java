package com.codewitheli.joblens.adapter.out.persistence;

import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory board store. MVP persistence — resets on restart. Swapped for a
 * PostgreSQL/JPA adapter in the persistence milestone.
 */
@Repository
public class InMemoryApplicationRepository implements ApplicationRepository {

    private final ConcurrentMap<String, ApplicationCard> store = new ConcurrentHashMap<>();

    @Override
    public ApplicationCard save(ApplicationCard card) {
        store.put(card.id(), card);
        return card;
    }

    @Override
    public List<ApplicationCard> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<ApplicationCard> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }
}

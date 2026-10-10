package com.codewitheli.joblens.adapter.out.persistence;

import com.codewitheli.joblens.application.port.out.ApplicationRepository;
import com.codewitheli.joblens.domain.model.ApplicationCard;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory board store, partitioned per client id. Default for demo/ai modes;
 * the JPA adapter takes over under the {@code postgres} profile. Resets on restart.
 */
@Repository
@Profile("!postgres")
public class InMemoryApplicationRepository implements ApplicationRepository {

    private final ConcurrentMap<String, ConcurrentMap<String, ApplicationCard>> store = new ConcurrentHashMap<>();

    private ConcurrentMap<String, ApplicationCard> board(String clientId) {
        return store.computeIfAbsent(clientId, k -> new ConcurrentHashMap<>());
    }

    @Override
    public ApplicationCard save(String clientId, ApplicationCard card) {
        board(clientId).put(card.id(), card);
        return card;
    }

    @Override
    public List<ApplicationCard> findAll(String clientId) {
        return List.copyOf(board(clientId).values());
    }

    @Override
    public Optional<ApplicationCard> findById(String clientId, String id) {
        return Optional.ofNullable(board(clientId).get(id));
    }

    @Override
    public boolean deleteById(String clientId, String id) {
        return board(clientId).remove(id) != null;
    }
}

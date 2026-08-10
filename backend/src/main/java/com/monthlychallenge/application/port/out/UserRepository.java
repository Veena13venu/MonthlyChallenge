package com.monthlychallenge.application.port.out;

import com.monthlychallenge.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port — persistence contract for {@link User}.
 * Implemented by the JPA adapter in the infrastructure layer.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByUsername(String username);

    List<User> searchByUsernameStartingWith(String prefix, UUID excludeUserId);

    boolean existsByUsername(String username);
}

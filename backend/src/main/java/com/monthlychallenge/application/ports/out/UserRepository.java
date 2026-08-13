package com.monthlychallenge.application.ports.out;

import com.monthlychallenge.domain.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> searchByUsernameStartingWith(String prefix, UUID excludeId);
}

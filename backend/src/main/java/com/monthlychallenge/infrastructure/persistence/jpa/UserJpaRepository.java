package com.monthlychallenge.infrastructure.persistence.jpa;

import com.monthlychallenge.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByKeycloakId(String keycloakId);

    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT(:prefix, '%')) AND u.id <> :excludeId")
    List<UserJpaEntity> searchByUsernameStartingWith(@Param("prefix") String prefix, @Param("excludeId") UUID excludeId);

    @Query("SELECT u FROM UserJpaEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<UserJpaEntity> searchByUsernameStartingWithNoExclude(@Param("prefix") String prefix);
}

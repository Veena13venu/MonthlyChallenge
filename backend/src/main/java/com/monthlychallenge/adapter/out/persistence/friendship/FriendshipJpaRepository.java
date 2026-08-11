package com.monthlychallenge.adapter.out.persistence.friendship;

import com.monthlychallenge.adapter.out.persistence.friendship.FriendshipJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipJpaEntity, UUID> {

    @Query("""
        SELECT f FROM FriendshipJpaEntity f
        WHERE (f.requesterId = :userA AND f.addresseeId = :userB)
           OR (f.requesterId = :userB AND f.addresseeId = :userA)
          AND f.status IN ('PENDING', 'ACCEPTED')
        """)
    Optional<FriendshipJpaEntity> findBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);

    @Query("""
        SELECT f FROM FriendshipJpaEntity f
        WHERE (f.requesterId = :userA AND f.addresseeId = :userB)
           OR (f.requesterId = :userB AND f.addresseeId = :userA)
        """)
    Optional<FriendshipJpaEntity> findBetweenAnyStatus(@Param("userA") UUID userA, @Param("userB") UUID userB);

    @Query("""
        SELECT f FROM FriendshipJpaEntity f
        WHERE (f.requesterId = :userId OR f.addresseeId = :userId)
          AND f.status = :status
        """)
    List<FriendshipJpaEntity> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") String status);

    @Query("""
        SELECT f FROM FriendshipJpaEntity f
        WHERE (f.requesterId = :userId OR f.addresseeId = :userId)
          AND f.status = 'ACCEPTED'
        """)
    List<FriendshipJpaEntity> findAcceptedFriends(@Param("userId") UUID userId);
}

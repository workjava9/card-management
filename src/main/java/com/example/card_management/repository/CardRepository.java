package com.example.card_management.repository;

import com.example.card_management.entity.CardEntity;
import com.example.card_management.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    @Query("""
        select c from CardEntity c
        where c.owner.id = :ownerId
          and (:status is null or c.status = :status)
          and (:last4 is null or c.last4 like concat('%', :last4))
    """)
    Page<CardEntity> findAllByOwnerFilter(@Param("ownerId") Long ownerId,
                                          @Param("status") CardStatus status,
                                          @Param("last4") String last4,
                                          Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CardEntity c where c.id = :id")
    Optional<CardEntity> findWithLockById(@Param("id") Long id);


    Optional<CardEntity> findByIdAndOwnerId(long id, Long ownerId);

    Optional<CardEntity> findByOwnerIdAndLast4(Long ownerId, String last4);
}


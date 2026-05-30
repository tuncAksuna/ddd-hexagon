package com.project.hexagonal.bid.infrastructure.repository;

import com.project.hexagonal.bid.infrastructure.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BidJpaRepository extends JpaRepository<BidEntity, UUID> {

    @Query("""
            SELECT b FROM BID b
            WHERE b.id = :offerId
            AND b.status != "CANCELLED"
            """)
    List<BidEntity> findByOfferId(UUID offerId);
}

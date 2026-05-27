package com.project.hexagonal.bid.infrastructure.repository;

import com.project.hexagonal.bid.infrastructure.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidJpaRepository extends JpaRepository<BidEntity, UUID> {

    List<BidEntity> findByOfferId(UUID offerId);
}

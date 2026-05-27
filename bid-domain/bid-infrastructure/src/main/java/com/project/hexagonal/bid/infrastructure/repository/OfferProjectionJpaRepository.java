package com.project.hexagonal.bid.infrastructure.repository;

import com.project.hexagonal.bid.infrastructure.entity.OfferProjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferProjectionJpaRepository extends JpaRepository<OfferProjectionEntity, UUID> {
}

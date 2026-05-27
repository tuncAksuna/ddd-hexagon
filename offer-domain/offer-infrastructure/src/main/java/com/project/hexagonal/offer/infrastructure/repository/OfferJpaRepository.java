package com.project.hexagonal.offer.infrastructure.repository;

import com.project.hexagonal.offer.infrastructure.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferJpaRepository extends JpaRepository<OfferEntity, UUID> {
}

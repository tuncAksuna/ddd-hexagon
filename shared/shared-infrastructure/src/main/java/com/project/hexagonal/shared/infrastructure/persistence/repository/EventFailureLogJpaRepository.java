package com.project.hexagonal.shared.infrastructure.persistence.repository;

import com.project.hexagonal.shared.infrastructure.persistence.entity.EventFailureLogEntity;
import com.project.hexagonal.shared.infrastructure.persistence.enums.EventFailureStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EventFailureLogJpaRepository extends JpaRepository<EventFailureLogEntity, Long> {

    List<EventFailureLogEntity> findByStatus(EventFailureStatus status);

}

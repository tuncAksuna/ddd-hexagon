package com.project.hexagonal.bid.infrastructure.entity;

import com.project.hexagonal.bid.core.valueobject.OfferProjectionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "offer_projection")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferProjectionEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OfferProjectionEnum status;
}

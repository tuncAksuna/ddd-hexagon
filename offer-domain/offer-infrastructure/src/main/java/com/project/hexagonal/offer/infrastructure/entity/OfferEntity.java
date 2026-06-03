package com.project.hexagonal.offer.infrastructure.entity;

import com.project.hexagonal.offer.core.valueobject.OfferStatus;
import com.project.hexagonal.shared.infrastructure.persistence.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(name = "offer")
@Entity(name = "OFFER")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OfferEntity extends AbstractBaseEntity {

    @Id
    @Column(name = "id",
            columnDefinition = "UUID",
            updatable = false,
            nullable = false)
    private UUID id;

    @Column(name = "CODE",
            unique = true,
            length = 50)
    private String code;

    @Column(name = "TITLE",
            nullable = false)
    private String title;

    @Column(name = "DESCRIPTION",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "START_DATE")
    private Instant startDate;

    @Column(name = "END_DATE")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS",
            length = 20)
    private OfferStatus status;

    @Column(name = "TOTAL_PRICE",
            precision = 19,
            scale = 2)
    private BigDecimal totalPrice;

}

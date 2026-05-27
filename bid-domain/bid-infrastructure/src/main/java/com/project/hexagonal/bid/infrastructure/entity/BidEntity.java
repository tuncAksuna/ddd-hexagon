package com.project.hexagonal.bid.infrastructure.entity;

import com.project.hexagonal.bid.core.valueobject.BidStatus;
import com.project.hexagonal.shared.infrastructure.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(name = "bid")
@Entity(name = "BID")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BidEntity extends AbstractBaseEntity {

    @Id
    @Column(name = "id",
            columnDefinition = "UUID",
            updatable = false,
            nullable = false)
    private UUID id;

    @Column(name = "OFFER_ID", nullable = false)
    private UUID offerId;

    @Column(name = "BIDDER_ID", nullable = false)
    private UUID bidderId;

    @Column(name = "SUBMITTED_AT", nullable = false)
    private Instant submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private BidStatus status;

    @Column(name = "TOTAL_PRICE", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalPrice;
}

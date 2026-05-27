package com.project.hexagonal.bid.core.valueobject;

import com.project.hexagonal.shared.core.valueobject.BaseId;

import java.util.UUID;

public class BidId extends BaseId<UUID> {

    public BidId(UUID val) {
        super(val);
    }
}

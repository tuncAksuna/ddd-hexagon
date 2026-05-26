package com.project.hexagonal.offer.core.valueobject;

import com.project.hexagonal.shared.core.valueobject.BaseId;

import java.util.UUID;

public class OfferId extends BaseId<UUID> {

    public OfferId(UUID val) {
        super(val);
    }

}

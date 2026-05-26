package com.project.hexagonal.shared.core.entity;

import java.util.Objects;

public abstract class BaseEntity<ID> {

    private ID val;

    public ID getId() {
        return val;
    }

    public void setId(ID val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "val=" + val +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}

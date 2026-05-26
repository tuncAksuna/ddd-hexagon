package com.project.hexagonal.shared.core.valueobject;

import java.util.Objects;

public abstract class BaseId<T> {

    private final T val;

    public T getVal() {
        return val;
    }

    public BaseId(T val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "BaseId{" +
                "val=" + val +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseId<?> baseId = (BaseId<?>) o;
        return Objects.equals(val, baseId.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}

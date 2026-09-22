package br.com.ubisafe.notification.domain.alert.model;

import java.util.Objects;
import java.util.UUID;

public record CorrelationId(UUID value) {

    public CorrelationId {
        Objects.requireNonNull(value, "value");
    }

    public static CorrelationId generate() {
        return new CorrelationId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

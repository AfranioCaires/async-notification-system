package br.com.ubisafe.alertprocessor.domain.notification.model;

import java.util.Objects;
import java.util.UUID;

public record CorrelationId(UUID value) {

    public CorrelationId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

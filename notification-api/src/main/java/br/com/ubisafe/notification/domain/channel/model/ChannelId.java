package br.com.ubisafe.notification.domain.channel.model;

import java.util.Objects;
import java.util.UUID;

public record ChannelId(UUID value) {

    public ChannelId {
        Objects.requireNonNull(value, "value");
    }

    public static ChannelId generate() {
        return new ChannelId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

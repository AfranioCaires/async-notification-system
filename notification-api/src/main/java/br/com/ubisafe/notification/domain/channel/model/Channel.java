package br.com.ubisafe.notification.domain.channel.model;

import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.alert.model.ClientId;
import br.com.ubisafe.notification.domain.alert.model.CorrelationId;
import br.com.ubisafe.notification.domain.channel.exception.ChannelInactiveException;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Channel {

    private final ChannelId id;
    private final Instant createdAt;
    private ChannelDefinition definition;
    private boolean active;
    private Instant updatedAt;
    private Instant deletedAt;

    private Channel(ChannelId id, ChannelDefinition definition, boolean active,
                    Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.definition = Objects.requireNonNull(definition, "definition");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.deletedAt = deletedAt;
    }

    public static Channel create(ChannelDefinition definition, boolean active, Instant now) {
        return new Channel(ChannelId.generate(), definition, active, now, now, null);
    }

    public static Channel restore(ChannelId id, ChannelDefinition definition, boolean active,
                                  Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new Channel(id, definition, active, createdAt, updatedAt, deletedAt);
    }

    public void update(ChannelDefinition definition, boolean active, Instant now) {
        this.definition = Objects.requireNonNull(definition, "definition");
        this.active = active;
        this.updatedAt = now;
    }

    public void delete(Instant now) {
        if (isDeleted()) {
            return;
        }
        this.active = false;
        this.deletedAt = now;
        this.updatedAt = now;
    }

    public AlertRequested requestAlert(ClientId clientId, Map<String, String> params,
                                       CorrelationId correlationId, Instant now) {
        if (!active || isDeleted()) {
            throw new ChannelInactiveException(id);
        }
        String message = definition.template().render(params);
        return new AlertRequested(
                correlationId,
                id,
                definition.type(),
                clientId,
                message,
                definition.config().priority(),
                definition.config().maxRetries(),
                now
        );
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public ChannelId id() {
        return id;
    }

    public ChannelDefinition definition() {
        return definition;
    }

    public String name() {
        return definition.name();
    }

    public ChannelType type() {
        return definition.type();
    }

    public Template template() {
        return definition.template();
    }

    public ChannelConfig config() {
        return definition.config();
    }

    public boolean isActive() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Optional<Instant> deletedAt() {
        return Optional.ofNullable(deletedAt);
    }
}

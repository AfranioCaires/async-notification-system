package br.com.ubisafe.alertprocessor.infrastructure.persistence;

import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

@Entity
@Immutable
@Table(name = "notification_events")
class NotificationEventJpaEntity implements Persistable<String> {

    @Id
    @Column(name = "id", nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "correlation_id", nullable = false, length = 36, updatable = false)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 16, updatable = false)
    private ChannelType channelType;

    @Column(name = "client_id", nullable = false, updatable = false)
    private long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16, updatable = false)
    private NotificationStatus status;

    @Column(name = "detail", nullable = false, length = 1000, updatable = false)
    private String detail;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected NotificationEventJpaEntity() {
    }

    NotificationEventJpaEntity(String id, String correlationId, ChannelType channelType, long clientId,
                               NotificationStatus status, String detail, Instant occurredAt) {
        this.id = id;
        this.correlationId = correlationId;
        this.channelType = channelType;
        this.clientId = clientId;
        this.status = status;
        this.detail = detail;
        this.occurredAt = occurredAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    String getCorrelationId() {
        return correlationId;
    }

    ChannelType getChannelType() {
        return channelType;
    }

    long getClientId() {
        return clientId;
    }

    NotificationStatus getStatus() {
        return status;
    }

    String getDetail() {
        return detail;
    }

    Instant getOccurredAt() {
        return occurredAt;
    }
}

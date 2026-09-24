package br.com.ubisafe.alertprocessor.infrastructure.persistence;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;

import java.util.UUID;

final class NotificationEventEntityMapper {

    private NotificationEventEntityMapper() {
    }

    static NotificationEventJpaEntity toEntity(NotificationEvent event) {
        return new NotificationEventJpaEntity(
                event.id().toString(),
                event.correlationId().toString(),
                event.channelType(),
                event.clientId(),
                event.status(),
                event.detail(),
                event.occurredAt()
        );
    }

    static NotificationEvent toDomain(NotificationEventJpaEntity entity) {
        return new NotificationEvent(
                UUID.fromString(entity.getId()),
                new CorrelationId(UUID.fromString(entity.getCorrelationId())),
                entity.getChannelType(),
                entity.getClientId(),
                entity.getStatus(),
                entity.getDetail(),
                entity.getOccurredAt()
        );
    }
}

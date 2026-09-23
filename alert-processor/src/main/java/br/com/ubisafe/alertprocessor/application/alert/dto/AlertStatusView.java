package br.com.ubisafe.alertprocessor.application.alert.dto;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AlertStatusView(
        UUID correlationId,
        ChannelType channelType,
        long clientId,
        NotificationStatus currentStatus,
        List<EventView> events
) {

    public record EventView(NotificationStatus status, String detail, Instant occurredAt) {

        static EventView from(NotificationEvent event) {
            return new EventView(event.status(), event.detail(), event.occurredAt());
        }
    }

    public static AlertStatusView from(NotificationHistory history) {
        NotificationEvent latest = history.latest().orElseThrow();
        return new AlertStatusView(
                history.correlationId().value(),
                latest.channelType(),
                latest.clientId(),
                latest.status(),
                history.events().stream().map(EventView::from).toList()
        );
    }
}

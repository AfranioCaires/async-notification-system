package br.com.ubisafe.alertprocessor.presentation.alert.response;

import br.com.ubisafe.alertprocessor.application.alert.dto.AlertStatusView;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AlertStatusResponse(
        UUID correlationId,
        ChannelType channelType,
        long clientId,
        NotificationStatus currentStatus,
        List<EventResponse> events
) {

    public record EventResponse(NotificationStatus status, String detail, Instant occurredAt) {
    }

    public static AlertStatusResponse from(AlertStatusView view) {
        return new AlertStatusResponse(
                view.correlationId(),
                view.channelType(),
                view.clientId(),
                view.currentStatus(),
                view.events().stream()
                        .map(event -> new EventResponse(event.status(), event.detail(), event.occurredAt()))
                        .toList()
        );
    }
}

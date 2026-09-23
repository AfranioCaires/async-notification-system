package br.com.ubisafe.alertprocessor.domain.notification.model;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class NotificationHistory {

    private static final Comparator<NotificationEvent> CHRONOLOGICAL = Comparator
            .comparing(NotificationEvent::occurredAt)
            .thenComparing(NotificationEvent::status);

    private final CorrelationId correlationId;
    private final List<NotificationEvent> events;

    public NotificationHistory(CorrelationId correlationId, List<NotificationEvent> events) {
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId");
        this.events = events.stream().sorted(CHRONOLOGICAL).toList();
    }

    public CorrelationId correlationId() {
        return correlationId;
    }

    public List<NotificationEvent> events() {
        return events;
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public Optional<NotificationStatus> currentStatus() {
        return latest().map(NotificationEvent::status);
    }

    public Optional<NotificationEvent> latest() {
        return events.isEmpty() ? Optional.empty() : Optional.of(events.getLast());
    }

    public boolean hasReached(NotificationStatus status) {
        return events.stream().anyMatch(event -> event.status() == status);
    }

    public boolean isConcluded() {
        return events.stream().anyMatch(event -> event.status().isTerminal());
    }
}

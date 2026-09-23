package br.com.ubisafe.alertprocessor.domain.notification.event;

import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record NotificationEvent(
        UUID id,
        CorrelationId correlationId,
        ChannelType channelType,
        long clientId,
        NotificationStatus status,
        String detail,
        Instant occurredAt
) {

    public static final int DETAIL_MAX_LENGTH = 1000;

    public NotificationEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(correlationId, "correlationId");
        Objects.requireNonNull(channelType, "channelType");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(occurredAt, "occurredAt");
        detail = truncate(Objects.requireNonNullElse(detail, ""));
    }

    public static NotificationEvent received(Alert alert, Instant occurredAt) {
        return of(alert, NotificationStatus.RECEBIDO, "Mensagem consumida do tópico", occurredAt);
    }

    public static NotificationEvent processing(Alert alert, Instant occurredAt) {
        return of(alert, NotificationStatus.PROCESSANDO,
                "Iniciando processamento via estratégia " + alert.channelType(), occurredAt);
    }

    public static NotificationEvent processed(Alert alert, Instant occurredAt) {
        return of(alert, NotificationStatus.PROCESSADO, "Processamento concluído com sucesso", occurredAt);
    }

    public static NotificationEvent failed(Alert alert, String reason, Instant occurredAt) {
        return of(alert, NotificationStatus.FALHA, "Processamento falhou: " + reason, occurredAt);
    }

    private static NotificationEvent of(Alert alert, NotificationStatus status, String detail, Instant occurredAt) {
        return new NotificationEvent(
                UUID.randomUUID(),
                alert.correlationId(),
                alert.channelType(),
                alert.clientId(),
                status,
                detail,
                occurredAt
        );
    }

    private static String truncate(String value) {
        return value.length() <= DETAIL_MAX_LENGTH ? value : value.substring(0, DETAIL_MAX_LENGTH);
    }
}

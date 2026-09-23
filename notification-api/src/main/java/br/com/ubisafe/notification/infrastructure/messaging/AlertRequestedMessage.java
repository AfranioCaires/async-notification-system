package br.com.ubisafe.notification.infrastructure.messaging;

import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

record AlertRequestedMessage(
        UUID correlationId,
        UUID channelId,
        ChannelType channelType,
        long clientId,
        String message,
        Priority priority,
        int maxRetries,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant requestedAt
) {

    static AlertRequestedMessage from(AlertRequested alert) {
        return new AlertRequestedMessage(
                alert.correlationId().value(),
                alert.channelId().value(),
                alert.channelType(),
                alert.clientId().value(),
                alert.message(),
                alert.priority(),
                alert.maxRetries(),
                alert.requestedAt()
        );
    }
}

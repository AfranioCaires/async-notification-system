package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.command.AlertData;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.Priority;
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

    AlertData toAlertData() {
        return new AlertData(correlationId, channelType, clientId, message, priority, requestedAt);
    }
}

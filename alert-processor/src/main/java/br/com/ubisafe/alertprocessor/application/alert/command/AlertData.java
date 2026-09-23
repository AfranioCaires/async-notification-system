package br.com.ubisafe.alertprocessor.application.alert.command;

import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.Priority;

import java.time.Instant;
import java.util.UUID;

public record AlertData(
        UUID correlationId,
        ChannelType channelType,
        long clientId,
        String message,
        Priority priority,
        Instant requestedAt
) {

    public Alert toAlert() {
        return new Alert(new CorrelationId(correlationId), channelType, clientId, message, priority, requestedAt);
    }
}

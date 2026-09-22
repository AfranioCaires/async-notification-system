package br.com.ubisafe.notification.domain.alert.event;

import br.com.ubisafe.notification.domain.alert.model.ClientId;
import br.com.ubisafe.notification.domain.alert.model.CorrelationId;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;

import java.time.Instant;

public record AlertRequested(
        CorrelationId correlationId,
        ChannelId channelId,
        ChannelType channelType,
        ClientId clientId,
        String message,
        Priority priority,
        int maxRetries,
        Instant requestedAt
) {
}

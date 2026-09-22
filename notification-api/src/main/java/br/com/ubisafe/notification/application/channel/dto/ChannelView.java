package br.com.ubisafe.notification.application.channel.dto;

import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;

import java.time.Instant;
import java.util.UUID;

public record ChannelView(
        UUID id,
        String name,
        ChannelType type,
        String template,
        int maxRetries,
        Priority priority,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static ChannelView from(Channel channel) {
        return new ChannelView(
                channel.id().value(),
                channel.name(),
                channel.type(),
                channel.template().value(),
                channel.config().maxRetries(),
                channel.config().priority(),
                channel.isActive(),
                channel.createdAt(),
                channel.updatedAt()
        );
    }
}

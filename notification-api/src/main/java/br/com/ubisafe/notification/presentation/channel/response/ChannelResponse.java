package br.com.ubisafe.notification.presentation.channel.response;

import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.presentation.channel.request.ChannelConfigPayload;

import java.time.Instant;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String name,
        ChannelType type,
        String template,
        ChannelConfigPayload config,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static ChannelResponse from(ChannelView view) {
        return new ChannelResponse(
                view.id(),
                view.name(),
                view.type(),
                view.template(),
                new ChannelConfigPayload(view.maxRetries(), view.priority()),
                view.active(),
                view.createdAt(),
                view.updatedAt()
        );
    }
}

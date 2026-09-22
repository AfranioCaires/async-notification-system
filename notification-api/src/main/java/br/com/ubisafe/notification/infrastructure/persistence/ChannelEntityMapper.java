package br.com.ubisafe.notification.infrastructure.persistence;

import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelConfig;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.model.Template;

import java.util.UUID;

final class ChannelEntityMapper {

    private ChannelEntityMapper() {
    }

    static ChannelJpaEntity toEntity(Channel channel, boolean newEntity) {
        return new ChannelJpaEntity(
                channel.id().toString(),
                channel.name(),
                channel.type(),
                channel.template().value(),
                channel.config().maxRetries(),
                channel.config().priority(),
                channel.isActive(),
                channel.createdAt(),
                channel.updatedAt(),
                channel.deletedAt().orElse(null),
                newEntity
        );
    }

    static Channel toDomain(ChannelJpaEntity entity) {
        ChannelDefinition definition = new ChannelDefinition(
                entity.getName(),
                entity.getType(),
                new Template(entity.getTemplate()),
                new ChannelConfig(entity.getMaxRetries(), entity.getPriority())
        );
        return Channel.restore(
                new ChannelId(UUID.fromString(entity.getId())),
                definition,
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}

package br.com.ubisafe.notification.domain.channel.service;

import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;

import java.util.Objects;

public class ChannelUniquenessPolicy {

    private final ChannelRepository repository;

    public ChannelUniquenessPolicy(ChannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public void ensureAvailable(ChannelDefinition definition) {
        if (repository.existsByNameAndType(definition.name(), definition.type())) {
            throw new ChannelAlreadyExistsException(definition.name(), definition.type());
        }
    }

    public void ensureAvailableFor(ChannelId channelId, ChannelDefinition definition) {
        if (repository.existsByNameAndTypeExcluding(definition.name(), definition.type(), channelId)) {
            throw new ChannelAlreadyExistsException(definition.name(), definition.type());
        }
    }
}

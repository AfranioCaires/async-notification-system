package br.com.ubisafe.notification.application.channel.service;

import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;

import java.util.Objects;

public class ChannelLookup {

    private final ChannelRepository repository;

    public ChannelLookup(ChannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Channel require(ChannelId channelId) {
        return repository.findById(channelId)
                .filter(channel -> !channel.isDeleted())
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }
}

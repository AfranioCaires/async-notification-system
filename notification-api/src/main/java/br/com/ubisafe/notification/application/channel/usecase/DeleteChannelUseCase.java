package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;

import java.time.Clock;
import java.util.UUID;

public class DeleteChannelUseCase {

    private final ChannelRepository repository;
    private final ChannelLookup channelLookup;
    private final Clock clock;

    public DeleteChannelUseCase(ChannelRepository repository, ChannelLookup channelLookup, Clock clock) {
        this.repository = repository;
        this.channelLookup = channelLookup;
        this.clock = clock;
    }

    public void execute(UUID channelId) {
        Channel channel = channelLookup.require(new ChannelId(channelId));
        channel.delete(clock.instant());
        repository.save(channel);
    }
}

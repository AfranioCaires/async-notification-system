package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.command.UpdateChannelCommand;
import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;

import java.time.Clock;

public class UpdateChannelUseCase {

    private final ChannelRepository repository;
    private final ChannelLookup channelLookup;
    private final ChannelUniquenessPolicy uniquenessPolicy;
    private final Clock clock;

    public UpdateChannelUseCase(ChannelRepository repository, ChannelLookup channelLookup,
                                ChannelUniquenessPolicy uniquenessPolicy, Clock clock) {
        this.repository = repository;
        this.channelLookup = channelLookup;
        this.uniquenessPolicy = uniquenessPolicy;
        this.clock = clock;
    }

    public ChannelView execute(UpdateChannelCommand command) {
        ChannelId channelId = new ChannelId(command.channelId());
        Channel channel = channelLookup.require(channelId);
        ChannelDefinition definition = command.data().toDefinition();
        uniquenessPolicy.ensureAvailableFor(channelId, definition);
        channel.update(definition, command.data().active(), clock.instant());
        return ChannelView.from(repository.save(channel));
    }
}

package br.com.ubisafe.notification.application.channel.usecase;

import br.com.ubisafe.notification.application.channel.command.CreateChannelCommand;
import br.com.ubisafe.notification.application.channel.dto.ChannelView;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;

import java.time.Clock;

public class CreateChannelUseCase {

    private final ChannelRepository repository;
    private final ChannelUniquenessPolicy uniquenessPolicy;
    private final Clock clock;

    public CreateChannelUseCase(ChannelRepository repository, ChannelUniquenessPolicy uniquenessPolicy, Clock clock) {
        this.repository = repository;
        this.uniquenessPolicy = uniquenessPolicy;
        this.clock = clock;
    }

    public ChannelView execute(CreateChannelCommand command) {
        ChannelDefinition definition = command.data().toDefinition();
        uniquenessPolicy.ensureAvailable(definition);
        Channel channel = Channel.create(definition, command.data().active(), clock.instant());
        return ChannelView.from(repository.save(channel));
    }
}

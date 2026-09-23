package br.com.ubisafe.notification.infrastructure.config;

import br.com.ubisafe.notification.application.alert.usecase.DispatchAlertUseCase;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.application.channel.usecase.CreateChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.DeleteChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.GetChannelUseCase;
import br.com.ubisafe.notification.application.channel.usecase.ListChannelsUseCase;
import br.com.ubisafe.notification.application.channel.usecase.UpdateChannelUseCase;
import br.com.ubisafe.notification.application.port.AlertPublisher;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import br.com.ubisafe.notification.domain.channel.service.ChannelUniquenessPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class UseCaseConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ChannelLookup channelLookup(ChannelRepository repository) {
        return new ChannelLookup(repository);
    }

    @Bean
    ChannelUniquenessPolicy channelUniquenessPolicy(ChannelRepository repository) {
        return new ChannelUniquenessPolicy(repository);
    }

    @Bean
    CreateChannelUseCase createChannelUseCase(ChannelRepository repository, ChannelUniquenessPolicy policy, Clock clock) {
        return new CreateChannelUseCase(repository, policy, clock);
    }

    @Bean
    GetChannelUseCase getChannelUseCase(ChannelLookup channelLookup) {
        return new GetChannelUseCase(channelLookup);
    }

    @Bean
    ListChannelsUseCase listChannelsUseCase(ChannelRepository repository) {
        return new ListChannelsUseCase(repository);
    }

    @Bean
    UpdateChannelUseCase updateChannelUseCase(ChannelRepository repository, ChannelLookup channelLookup,
                                              ChannelUniquenessPolicy policy, Clock clock) {
        return new UpdateChannelUseCase(repository, channelLookup, policy, clock);
    }

    @Bean
    DeleteChannelUseCase deleteChannelUseCase(ChannelRepository repository, ChannelLookup channelLookup, Clock clock) {
        return new DeleteChannelUseCase(repository, channelLookup, clock);
    }

    @Bean
    DispatchAlertUseCase dispatchAlertUseCase(ChannelLookup channelLookup, AlertPublisher alertPublisher, Clock clock) {
        return new DispatchAlertUseCase(channelLookup, alertPublisher, clock);
    }
}

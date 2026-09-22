package br.com.ubisafe.notification.application.alert.usecase;

import br.com.ubisafe.notification.application.alert.command.DispatchAlertCommand;
import br.com.ubisafe.notification.application.alert.dto.DispatchAlertResult;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.application.port.AlertPublisher;
import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.alert.model.ClientId;
import br.com.ubisafe.notification.domain.alert.model.CorrelationId;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;

import java.time.Clock;

public class DispatchAlertUseCase {

    private final ChannelLookup channelLookup;
    private final AlertPublisher alertPublisher;
    private final Clock clock;

    public DispatchAlertUseCase(ChannelLookup channelLookup, AlertPublisher alertPublisher, Clock clock) {
        this.channelLookup = channelLookup;
        this.alertPublisher = alertPublisher;
        this.clock = clock;
    }

    public DispatchAlertResult execute(DispatchAlertCommand command) {
        Channel channel = channelLookup.require(new ChannelId(command.channelId()));
        AlertRequested alert = channel.requestAlert(
                new ClientId(command.clientId()),
                command.params(),
                CorrelationId.generate(),
                clock.instant()
        );
        alertPublisher.publish(alert);
        return new DispatchAlertResult(alert.correlationId().value());
    }
}

package br.com.ubisafe.notification.domain.channel.exception;

import br.com.ubisafe.notification.domain.channel.model.ChannelId;
import br.com.ubisafe.notification.domain.shared.exception.DomainException;

public class ChannelInactiveException extends DomainException {

    public ChannelInactiveException(ChannelId channelId) {
        super("O canal " + channelId + " está inativo e não aceita disparos");
    }
}

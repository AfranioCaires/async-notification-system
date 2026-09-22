package br.com.ubisafe.notification.application.channel.exception;

import br.com.ubisafe.notification.application.shared.exception.ApplicationException;
import br.com.ubisafe.notification.domain.channel.model.ChannelId;

public class ChannelNotFoundException extends ApplicationException {

    public ChannelNotFoundException(ChannelId channelId) {
        super("Canal " + channelId + " não encontrado");
    }
}

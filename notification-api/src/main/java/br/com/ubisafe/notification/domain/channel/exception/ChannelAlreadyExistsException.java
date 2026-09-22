package br.com.ubisafe.notification.domain.channel.exception;

import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.shared.exception.DomainException;

public class ChannelAlreadyExistsException extends DomainException {

    public ChannelAlreadyExistsException(String name, ChannelType type) {
        super("Já existe um canal com o nome '" + name + "' e tipo " + type);
    }
}

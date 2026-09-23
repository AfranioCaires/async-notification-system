package br.com.ubisafe.alertprocessor.application.alert.exception;

import br.com.ubisafe.alertprocessor.application.shared.exception.ApplicationException;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;

public class UnsupportedChannelTypeException extends ApplicationException {

    public UnsupportedChannelTypeException(ChannelType channelType) {
        super("Nenhuma estratégia de entrega registrada para o tipo " + channelType);
    }
}

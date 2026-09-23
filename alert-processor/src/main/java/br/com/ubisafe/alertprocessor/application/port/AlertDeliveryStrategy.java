package br.com.ubisafe.alertprocessor.application.port;

import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;

public interface AlertDeliveryStrategy {

    ChannelType channelType();

    void deliver(Alert alert);
}

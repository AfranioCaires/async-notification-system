package br.com.ubisafe.alertprocessor.infrastructure.delivery;

import br.com.ubisafe.alertprocessor.application.port.DeliveryFailedException;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import org.springframework.stereotype.Component;

@Component
class DeliverySimulator {

    private final DeliverySimulationProperties properties;

    DeliverySimulator(DeliverySimulationProperties properties) {
        this.properties = properties;
    }

    void send(Alert alert) {
        if (properties.failingClientIds().contains(alert.clientId())) {
            throw new DeliveryFailedException("Provedor " + alert.channelType()
                    + " recusou a entrega para o cliente " + alert.clientId());
        }
    }
}

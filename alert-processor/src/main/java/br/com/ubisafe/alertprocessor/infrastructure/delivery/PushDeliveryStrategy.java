package br.com.ubisafe.alertprocessor.infrastructure.delivery;

import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class PushDeliveryStrategy implements AlertDeliveryStrategy {

    private static final Logger log = LoggerFactory.getLogger(PushDeliveryStrategy.class);

    private final DeliverySimulator simulator;

    PushDeliveryStrategy(DeliverySimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.PUSH;
    }

    @Override
    public void deliver(Alert alert) {
        simulator.send(alert);
        String deliveryPriority = alert.priority() == Priority.HIGH ? "high" : "normal";
        log.info("Push enviado ao cliente {} com prioridade de entrega {}", alert.clientId(), deliveryPriority);
    }
}

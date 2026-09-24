package br.com.ubisafe.alertprocessor.infrastructure.delivery;

import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class SmsDeliveryStrategy implements AlertDeliveryStrategy {

    private static final Logger log = LoggerFactory.getLogger(SmsDeliveryStrategy.class);
    private static final int SEGMENT_LENGTH = 160;

    private final DeliverySimulator simulator;

    SmsDeliveryStrategy(DeliverySimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    @Override
    public void deliver(Alert alert) {
        simulator.send(alert);
        int segments = (int) Math.ceil((double) alert.message().length() / SEGMENT_LENGTH);
        log.info("SMS enviado ao cliente {} em {} segmento(s)", alert.clientId(), segments);
    }
}

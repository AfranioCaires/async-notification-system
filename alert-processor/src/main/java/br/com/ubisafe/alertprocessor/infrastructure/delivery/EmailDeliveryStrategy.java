package br.com.ubisafe.alertprocessor.infrastructure.delivery;

import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class EmailDeliveryStrategy implements AlertDeliveryStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmailDeliveryStrategy.class);
    private static final int SUBJECT_LENGTH = 60;

    private final DeliverySimulator simulator;

    EmailDeliveryStrategy(DeliverySimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public void deliver(Alert alert) {
        simulator.send(alert);
        String subject = alert.message().length() > SUBJECT_LENGTH
                ? alert.message().substring(0, SUBJECT_LENGTH) + "..."
                : alert.message();
        log.info("E-mail enviado ao cliente {} com assunto '{}' e prioridade {}",
                alert.clientId(), subject, alert.priority());
    }
}

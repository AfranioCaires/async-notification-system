package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.command.ProcessAlertCommand;
import br.com.ubisafe.alertprocessor.application.alert.dto.ProcessingOutcome;
import br.com.ubisafe.alertprocessor.application.alert.usecase.ProcessAlertUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class AlertRequestedListener {

    static final String CORRELATION_ID = "correlationId";

    private static final Logger log = LoggerFactory.getLogger(AlertRequestedListener.class);

    private final ProcessAlertUseCase processAlert;

    AlertRequestedListener(ProcessAlertUseCase processAlert) {
        this.processAlert = processAlert;
    }

    @KafkaListener(topics = "${app.kafka.alerts-topic}")
    void onAlertRequested(AlertRequestedMessage message) {
        try (MDC.MDCCloseable ignored = MDC.putCloseable(CORRELATION_ID, message.correlationId().toString())) {
            ProcessingOutcome outcome = processAlert.execute(new ProcessAlertCommand(message.toAlertData()));
            log.info("Alerta {} via {}", outcome, message.channelType());
        }
    }
}

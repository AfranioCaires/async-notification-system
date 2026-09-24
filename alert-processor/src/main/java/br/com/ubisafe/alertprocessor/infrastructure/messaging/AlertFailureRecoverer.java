package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.command.RecordAlertFailureCommand;
import br.com.ubisafe.alertprocessor.application.alert.usecase.RecordAlertFailureUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;

class AlertFailureRecoverer implements ConsumerRecordRecoverer {

    private static final Logger log = LoggerFactory.getLogger(AlertFailureRecoverer.class);

    private final RecordAlertFailureUseCase recordAlertFailure;
    private final ConsumerRecordRecoverer deadLetterRecoverer;

    AlertFailureRecoverer(RecordAlertFailureUseCase recordAlertFailure, ConsumerRecordRecoverer deadLetterRecoverer) {
        this.recordAlertFailure = recordAlertFailure;
        this.deadLetterRecoverer = deadLetterRecoverer;
    }

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        String reason = reasonOf(exception);
        if (record.value() instanceof AlertRequestedMessage message) {
            try (MDC.MDCCloseable ignored = MDC.putCloseable(AlertRequestedListener.CORRELATION_ID, message.correlationId().toString())) {
                recordAlertFailure.execute(new RecordAlertFailureCommand(message.toAlertData(), reason));
                deadLetterRecoverer.accept(record, exception);
                log.warn("Alerta encaminhado para a DLQ: {}", reason);
            }
            return;
        }
        deadLetterRecoverer.accept(record, exception);
        log.warn("Mensagem ilegível encaminhada para a DLQ: {}", reason);
    }

    private static String reasonOf(Exception exception) {
        Throwable cause = exception instanceof ListenerExecutionFailedException && exception.getCause() != null
                ? exception.getCause()
                : exception;
        return cause.getClass().getSimpleName() + ": " + cause.getMessage();
    }
}

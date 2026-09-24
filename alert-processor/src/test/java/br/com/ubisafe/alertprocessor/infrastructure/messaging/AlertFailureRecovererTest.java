package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.command.RecordAlertFailureCommand;
import br.com.ubisafe.alertprocessor.application.alert.usecase.RecordAlertFailureUseCase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertFailureRecovererTest {

    @Mock
    private RecordAlertFailureUseCase recordAlertFailure;

    @Mock
    private ConsumerRecordRecoverer deadLetterRecoverer;

    private AlertFailureRecoverer recoverer;

    @BeforeEach
    void setUp() {
        recoverer = new AlertFailureRecoverer(recordAlertFailure, deadLetterRecoverer);
    }

    @Test
    void shouldRecordFailureAndPublishToDeadLetter() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 10, "12345", AlertMessages.withMaxRetries(3));
        IllegalStateException exception = new IllegalStateException("provedor fora do ar");

        recoverer.accept(record, exception);

        ArgumentCaptor<RecordAlertFailureCommand> captor = ArgumentCaptor.forClass(RecordAlertFailureCommand.class);
        verify(recordAlertFailure).execute(captor.capture());
        assertThat(captor.getValue().alert().correlationId()).isEqualTo(CORRELATION_ID);
        assertThat(captor.getValue().reason()).contains("provedor fora do ar");
        verify(deadLetterRecoverer).accept(record, exception);
    }

    @Test
    void shouldStillPublishUnreadableMessageToDeadLetter() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 11, "k", null);
        IllegalStateException exception = new IllegalStateException("desserialização");

        recoverer.accept(record, exception);

        verify(recordAlertFailure, never()).execute(any());
        verify(deadLetterRecoverer).accept(record, exception);
    }
}

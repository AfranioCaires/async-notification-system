package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.command.RecordAlertFailureCommand;
import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alert;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alertData;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordAlertFailureUseCaseTest {

    @Mock
    private NotificationEventRepository repository;

    private RecordAlertFailureUseCase useCase;
    private final Alert alert = alert(ChannelType.SMS);

    @BeforeEach
    void setUp() {
        useCase = new RecordAlertFailureUseCase(repository, fixedClock());
    }

    @Test
    void shouldAppendFailureAfterProcessing() {
        when(repository.historyOf(alert.correlationId())).thenReturn(new NotificationHistory(alert.correlationId(),
                List.of(NotificationEvent.received(alert, NOW), NotificationEvent.processing(alert, NOW))));

        useCase.execute(new RecordAlertFailureCommand(alertData(ChannelType.SMS), "timeout"));

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(repository).append(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(NotificationStatus.FALHA);
        assertThat(captor.getValue().detail()).contains("timeout");
    }

    @Test
    void shouldRecordReceptionBeforeFailureWhenMissing() {
        when(repository.historyOf(alert.correlationId())).thenReturn(new NotificationHistory(alert.correlationId(), List.of()));

        useCase.execute(new RecordAlertFailureCommand(alertData(ChannelType.SMS), "sem estratégia"));

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(repository, times(2)).append(captor.capture());
        assertThat(captor.getAllValues()).extracting(NotificationEvent::status)
                .containsExactly(NotificationStatus.RECEBIDO, NotificationStatus.FALHA);
    }

    @Test
    void shouldIgnoreAlreadyConcludedAlert() {
        when(repository.historyOf(alert.correlationId())).thenReturn(new NotificationHistory(alert.correlationId(),
                List.of(NotificationEvent.processed(alert, NOW))));

        useCase.execute(new RecordAlertFailureCommand(alertData(ChannelType.SMS), "timeout"));

        verify(repository, never()).append(any());
    }
}

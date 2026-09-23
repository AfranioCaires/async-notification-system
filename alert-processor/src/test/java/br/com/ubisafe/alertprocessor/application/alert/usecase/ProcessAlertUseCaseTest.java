package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.command.ProcessAlertCommand;
import br.com.ubisafe.alertprocessor.application.alert.dto.ProcessingOutcome;
import br.com.ubisafe.alertprocessor.application.alert.exception.UnsupportedChannelTypeException;
import br.com.ubisafe.alertprocessor.application.alert.service.DeliveryStrategyResolver;
import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.application.port.DeliveryFailedException;
import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alert;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alertData;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessAlertUseCaseTest {

    @Mock
    private NotificationEventRepository repository;

    @Mock
    private AlertDeliveryStrategy emailStrategy;

    private ProcessAlertUseCase useCase;

    @BeforeEach
    void setUp() {
        when(emailStrategy.channelType()).thenReturn(ChannelType.EMAIL);
        useCase = new ProcessAlertUseCase(repository, new DeliveryStrategyResolver(List.of(emailStrategy)), fixedClock());
    }

    @Test
    void shouldAppendFullLifecycleAndDeliver() {
        when(repository.historyOf(new CorrelationId(CORRELATION_ID))).thenReturn(history());

        ProcessingOutcome outcome = useCase.execute(new ProcessAlertCommand(alertData(ChannelType.EMAIL)));

        assertThat(outcome).isEqualTo(ProcessingOutcome.PROCESSED);
        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        InOrder order = inOrder(repository, emailStrategy);
        order.verify(repository, times(2)).append(captor.capture());
        order.verify(emailStrategy).deliver(any(Alert.class));
        order.verify(repository).append(captor.capture());
        assertThat(captor.getAllValues()).extracting(NotificationEvent::status).containsExactly(
                NotificationStatus.RECEBIDO, NotificationStatus.PROCESSANDO, NotificationStatus.PROCESSADO);
    }

    @Test
    void shouldSkipAlreadyConcludedAlert() {
        Alert alert = alert(ChannelType.EMAIL);
        when(repository.historyOf(alert.correlationId())).thenReturn(history(
                NotificationEvent.received(alert, NOW),
                NotificationEvent.processing(alert, NOW),
                NotificationEvent.processed(alert, NOW)));

        ProcessingOutcome outcome = useCase.execute(new ProcessAlertCommand(alertData(ChannelType.EMAIL)));

        assertThat(outcome).isEqualTo(ProcessingOutcome.ALREADY_CONCLUDED);
        verify(repository, never()).append(any());
        verify(emailStrategy, never()).deliver(any());
    }

    @Test
    void shouldResumeRetryWithoutDuplicatingEvents() {
        Alert alert = alert(ChannelType.EMAIL);
        when(repository.historyOf(alert.correlationId())).thenReturn(history(
                NotificationEvent.received(alert, NOW),
                NotificationEvent.processing(alert, NOW)));

        useCase.execute(new ProcessAlertCommand(alertData(ChannelType.EMAIL)));

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(repository).append(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(NotificationStatus.PROCESSADO);
    }

    @Test
    void shouldPropagateDeliveryFailureWithoutMarkingProcessed() {
        when(repository.historyOf(any())).thenReturn(history());
        doThrow(new DeliveryFailedException("timeout")).when(emailStrategy).deliver(any());

        assertThatThrownBy(() -> useCase.execute(new ProcessAlertCommand(alertData(ChannelType.EMAIL))))
                .isInstanceOf(DeliveryFailedException.class);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(repository, times(2)).append(captor.capture());
        assertThat(captor.getAllValues()).extracting(NotificationEvent::status)
                .doesNotContain(NotificationStatus.PROCESSADO);
    }

    @Test
    void shouldFailFastForChannelWithoutStrategy() {
        when(repository.historyOf(any())).thenReturn(history());

        assertThatThrownBy(() -> useCase.execute(new ProcessAlertCommand(alertData(ChannelType.PUSH))))
                .isInstanceOf(UnsupportedChannelTypeException.class);
    }

    private static NotificationHistory history(NotificationEvent... events) {
        return new NotificationHistory(new CorrelationId(CORRELATION_ID), List.of(events));
    }
}

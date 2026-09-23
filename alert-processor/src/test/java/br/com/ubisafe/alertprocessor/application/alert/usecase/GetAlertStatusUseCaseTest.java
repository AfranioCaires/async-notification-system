package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.dto.AlertStatusView;
import br.com.ubisafe.alertprocessor.application.alert.exception.AlertNotFoundException;
import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAlertStatusUseCaseTest {

    @Mock
    private NotificationEventRepository repository;

    @InjectMocks
    private GetAlertStatusUseCase useCase;

    @Test
    void shouldReturnStatusDerivedFromLatestEvent() {
        Alert alert = alert(ChannelType.EMAIL);
        when(repository.historyOf(alert.correlationId())).thenReturn(new NotificationHistory(alert.correlationId(), List.of(
                NotificationEvent.received(alert, NOW),
                NotificationEvent.processing(alert, NOW.plusSeconds(1)),
                NotificationEvent.failed(alert, "timeout", NOW.plusSeconds(2)))));

        AlertStatusView view = useCase.execute(CORRELATION_ID);

        assertThat(view.currentStatus()).isEqualTo(NotificationStatus.FALHA);
        assertThat(view.channelType()).isEqualTo(ChannelType.EMAIL);
        assertThat(view.clientId()).isEqualTo(12345L);
        assertThat(view.events()).hasSize(3);
    }

    @Test
    void shouldFailWhenThereAreNoEvents() {
        Alert alert = alert(ChannelType.EMAIL);
        when(repository.historyOf(alert.correlationId())).thenReturn(new NotificationHistory(alert.correlationId(), List.of()));

        assertThatThrownBy(() -> useCase.execute(CORRELATION_ID)).isInstanceOf(AlertNotFoundException.class);
    }
}

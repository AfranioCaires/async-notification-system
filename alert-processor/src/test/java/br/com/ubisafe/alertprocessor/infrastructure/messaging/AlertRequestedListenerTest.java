package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.command.ProcessAlertCommand;
import br.com.ubisafe.alertprocessor.application.alert.dto.ProcessingOutcome;
import br.com.ubisafe.alertprocessor.application.alert.usecase.ProcessAlertUseCase;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertRequestedListenerTest {

    @Mock
    private ProcessAlertUseCase processAlert;

    @InjectMocks
    private AlertRequestedListener listener;

    @Test
    void shouldProcessMessageAsAlert() {
        when(processAlert.execute(any())).thenReturn(ProcessingOutcome.PROCESSED);

        listener.onAlertRequested(AlertMessages.withMaxRetries(3));

        ArgumentCaptor<ProcessAlertCommand> captor = ArgumentCaptor.forClass(ProcessAlertCommand.class);
        verify(processAlert).execute(captor.capture());
        assertThat(captor.getValue().alert().correlationId()).isEqualTo(CORRELATION_ID);
        assertThat(captor.getValue().alert().channelType()).isEqualTo(ChannelType.EMAIL);
        assertThat(captor.getValue().alert().clientId()).isEqualTo(12345L);
    }
}

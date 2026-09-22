package br.com.ubisafe.notification.application.alert.usecase;

import br.com.ubisafe.notification.application.alert.command.DispatchAlertCommand;
import br.com.ubisafe.notification.application.alert.dto.DispatchAlertResult;
import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.application.channel.service.ChannelLookup;
import br.com.ubisafe.notification.application.port.AlertPublisher;
import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.channel.exception.ChannelInactiveException;
import br.com.ubisafe.notification.domain.channel.exception.MissingTemplateParametersException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.repository.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.NOW;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.fixedClock;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.inactiveChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchAlertUseCaseTest {

    private static final Map<String, String> PARAMS = Map.of("clientName", "João Silva", "billingMonth", "Novembro/2025");

    @Mock
    private ChannelRepository repository;

    @Mock
    private AlertPublisher publisher;

    private DispatchAlertUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DispatchAlertUseCase(new ChannelLookup(repository), publisher, fixedClock());
    }

    @Test
    void shouldPublishRenderedAlertAndReturnCorrelationId() {
        Channel channel = activeChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));

        DispatchAlertResult result = useCase.execute(new DispatchAlertCommand(channel.id().value(), 12345L, PARAMS));

        ArgumentCaptor<AlertRequested> captor = ArgumentCaptor.forClass(AlertRequested.class);
        verify(publisher).publish(captor.capture());
        AlertRequested published = captor.getValue();
        assertThat(published.correlationId().value()).isEqualTo(result.correlationId());
        assertThat(published.message()).isEqualTo("Olá João Silva, sua fatura de Novembro/2025 está disponível.");
        assertThat(published.clientId().value()).isEqualTo(12345L);
        assertThat(published.requestedAt()).isEqualTo(NOW);
    }

    @Test
    void shouldRejectUnknownChannel() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new DispatchAlertCommand(UUID.randomUUID(), 1L, PARAMS)))
                .isInstanceOf(ChannelNotFoundException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void shouldRejectInactiveChannel() {
        Channel channel = inactiveChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));

        assertThatThrownBy(() -> useCase.execute(new DispatchAlertCommand(channel.id().value(), 1L, PARAMS)))
                .isInstanceOf(ChannelInactiveException.class);
        verify(publisher, never()).publish(any());
    }

    @Test
    void shouldRejectMissingParams() {
        Channel channel = activeChannel();
        when(repository.findById(channel.id())).thenReturn(Optional.of(channel));

        assertThatThrownBy(() -> useCase.execute(new DispatchAlertCommand(channel.id().value(), 1L, Map.of())))
                .isInstanceOf(MissingTemplateParametersException.class);
        verify(publisher, never()).publish(any());
    }
}

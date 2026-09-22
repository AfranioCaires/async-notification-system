package br.com.ubisafe.notification.domain.channel;

import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.alert.model.ClientId;
import br.com.ubisafe.notification.domain.alert.model.CorrelationId;
import br.com.ubisafe.notification.domain.channel.exception.ChannelInactiveException;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelConfig;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import br.com.ubisafe.notification.domain.channel.model.Template;
import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.NOW;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.definition;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.inactiveChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelTest {

    private static final Map<String, String> PARAMS = Map.of("clientName", "João", "billingMonth", "Novembro/2025");

    @Test
    void shouldCreateChannelWithTimestamps() {
        Channel channel = activeChannel();

        assertThat(channel.id()).isNotNull();
        assertThat(channel.createdAt()).isEqualTo(NOW);
        assertThat(channel.updatedAt()).isEqualTo(NOW);
        assertThat(channel.isDeleted()).isFalse();
    }

    @Test
    void shouldUpdateDefinitionAndTimestamp() {
        Channel channel = activeChannel();
        Instant later = NOW.plusSeconds(60);
        ChannelDefinition newDefinition = new ChannelDefinition("Cobrança", ChannelType.SMS,
                new Template("Pague {{valor}}"), new ChannelConfig(1, Priority.LOW));

        channel.update(newDefinition, false, later);

        assertThat(channel.name()).isEqualTo("Cobrança");
        assertThat(channel.type()).isEqualTo(ChannelType.SMS);
        assertThat(channel.isActive()).isFalse();
        assertThat(channel.updatedAt()).isEqualTo(later);
        assertThat(channel.createdAt()).isEqualTo(NOW);
    }

    @Test
    void shouldDeleteLogicallyAndDeactivate() {
        Channel channel = activeChannel();
        Instant later = NOW.plusSeconds(60);

        channel.delete(later);

        assertThat(channel.isDeleted()).isTrue();
        assertThat(channel.isActive()).isFalse();
        assertThat(channel.deletedAt()).contains(later);
    }

    @Test
    void shouldRequestAlertWithRenderedMessage() {
        Channel channel = activeChannel();
        CorrelationId correlationId = CorrelationId.generate();

        AlertRequested alert = channel.requestAlert(new ClientId(12345L), PARAMS, correlationId, NOW);

        assertThat(alert.correlationId()).isEqualTo(correlationId);
        assertThat(alert.channelId()).isEqualTo(channel.id());
        assertThat(alert.channelType()).isEqualTo(ChannelType.EMAIL);
        assertThat(alert.message()).isEqualTo("Olá João, sua fatura de Novembro/2025 está disponível.");
        assertThat(alert.maxRetries()).isEqualTo(3);
        assertThat(alert.priority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void shouldRejectAlertOnInactiveChannel() {
        Channel channel = inactiveChannel();

        assertThatThrownBy(() -> channel.requestAlert(new ClientId(1L), PARAMS, CorrelationId.generate(), NOW))
                .isInstanceOf(ChannelInactiveException.class);
    }

    @Test
    void shouldRejectAlertOnDeletedChannel() {
        Channel channel = activeChannel();
        channel.delete(NOW);

        assertThatThrownBy(() -> channel.requestAlert(new ClientId(1L), PARAMS, CorrelationId.generate(), NOW))
                .isInstanceOf(ChannelInactiveException.class);
    }

    @Test
    void shouldStripChannelName() {
        ChannelDefinition definition = new ChannelDefinition("  Fatura  ", ChannelType.EMAIL,
                definition().template(), definition().config());

        assertThat(definition.name()).isEqualTo("Fatura");
    }

    @Test
    void shouldRejectInvalidMaxRetries() {
        assertThatThrownBy(() -> new ChannelConfig(11, Priority.HIGH)).isInstanceOf(DomainValidationException.class);
        assertThatThrownBy(() -> new ChannelConfig(-1, Priority.HIGH)).isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldRejectNonPositiveClientId() {
        assertThatThrownBy(() -> new ClientId(0)).isInstanceOf(DomainValidationException.class);
    }
}

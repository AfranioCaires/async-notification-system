package br.com.ubisafe.notification.infrastructure.messaging;

import br.com.ubisafe.notification.application.port.AlertPublicationException;
import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import br.com.ubisafe.notification.domain.alert.model.ClientId;
import br.com.ubisafe.notification.domain.alert.model.CorrelationId;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static br.com.ubisafe.notification.fixture.ChannelFixtures.NOW;
import static br.com.ubisafe.notification.fixture.ChannelFixtures.activeChannel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaAlertPublisherTest {

    private static final String TOPIC = "notifications.alerts.requested";

    @Mock
    private KafkaTemplate<String, AlertRequestedMessage> kafkaTemplate;

    private KafkaAlertPublisher publisher;
    private AlertRequested alert;

    @BeforeEach
    void setUp() {
        publisher = new KafkaAlertPublisher(kafkaTemplate, new AlertTopicProperties(TOPIC, Duration.ofSeconds(1)));
        alert = activeChannel().requestAlert(new ClientId(12345L),
                Map.of("clientName", "João", "billingMonth", "Nov"), CorrelationId.generate(), NOW);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldPublishMessageKeyedByClientWithHeaders() {
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));

        publisher.publish(alert);

        ArgumentCaptor<ProducerRecord<String, AlertRequestedMessage>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        ProducerRecord<String, AlertRequestedMessage> record = captor.getValue();
        assertThat(record.topic()).isEqualTo(TOPIC);
        assertThat(record.key()).isEqualTo("12345");
        assertThat(header(record, AlertHeaders.CORRELATION_ID)).isEqualTo(alert.correlationId().toString());
        assertThat(record.value().correlationId()).isEqualTo(alert.correlationId().value());
        assertThat(record.value().channelType()).isEqualTo(ChannelType.EMAIL);
        assertThat(record.value().message()).isEqualTo(alert.message());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldTranslateBrokerFailure() {
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));

        assertThatThrownBy(() -> publisher.publish(alert)).isInstanceOf(AlertPublicationException.class);
    }

    private static String header(ProducerRecord<String, AlertRequestedMessage> record, String name) {
        return new String(record.headers().lastHeader(name).value(), StandardCharsets.UTF_8);
    }
}

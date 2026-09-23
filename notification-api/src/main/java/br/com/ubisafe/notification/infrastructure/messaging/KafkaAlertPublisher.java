package br.com.ubisafe.notification.infrastructure.messaging;

import br.com.ubisafe.notification.application.port.AlertPublicationException;
import br.com.ubisafe.notification.application.port.AlertPublisher;
import br.com.ubisafe.notification.domain.alert.event.AlertRequested;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
class KafkaAlertPublisher implements AlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaAlertPublisher.class);

    private final KafkaTemplate<String, AlertRequestedMessage> kafkaTemplate;
    private final AlertTopicProperties topic;

    KafkaAlertPublisher(KafkaTemplate<String, AlertRequestedMessage> kafkaTemplate, AlertTopicProperties topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(AlertRequested alert) {
        String correlationId = alert.correlationId().toString();
        try (MDC.MDCCloseable ignored = MDC.putCloseable(AlertHeaders.CORRELATION_ID, correlationId)) {
            kafkaTemplate.send(toRecord(alert)).get(topic.publishTimeout().toMillis(), TimeUnit.MILLISECONDS);
            log.info("Alerta publicado no tópico {} para o canal {} ({})",
                    topic.name(), alert.channelId(), alert.channelType());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AlertPublicationException("Publicação do alerta interrompida", exception);
        } catch (ExecutionException | TimeoutException exception) {
            log.error("Falha ao publicar alerta no tópico {}", topic.name(), exception);
            throw new AlertPublicationException("Não foi possível publicar o alerta no momento", exception);
        }
    }

    private ProducerRecord<String, AlertRequestedMessage> toRecord(AlertRequested alert) {
        ProducerRecord<String, AlertRequestedMessage> record = new ProducerRecord<>(
                topic.name(),
                String.valueOf(alert.clientId().value()),
                AlertRequestedMessage.from(alert)
        );
        record.headers()
                .add(AlertHeaders.CORRELATION_ID, bytes(alert.correlationId().toString()))
                .add(AlertHeaders.EVENT_TYPE, bytes(AlertHeaders.ALERT_REQUESTED_V1));
        return record;
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}

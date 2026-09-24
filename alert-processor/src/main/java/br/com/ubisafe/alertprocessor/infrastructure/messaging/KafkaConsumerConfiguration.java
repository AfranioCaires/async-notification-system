package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.application.alert.exception.UnsupportedChannelTypeException;
import br.com.ubisafe.alertprocessor.application.alert.usecase.RecordAlertFailureUseCase;
import br.com.ubisafe.alertprocessor.domain.shared.exception.DomainValidationException;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
class KafkaConsumerConfiguration {

    @Bean
    DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(ProducerFactory<Object, Object> producerFactory,
                                                                KafkaTemplate<Object, Object> jsonTemplate,
                                                                AlertKafkaProperties properties) {
        KafkaTemplate<Object, Object> rawTemplate = new KafkaTemplate<>(producerFactory,
                Map.of(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class));
        Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, rawTemplate);
        templates.put(Object.class, jsonTemplate);
        return new DeadLetterPublishingRecoverer(templates,
                (record, exception) -> new TopicPartition(properties.deadLetterTopic(), -1));
    }

    @Bean
    DefaultErrorHandler alertErrorHandler(RecordAlertFailureUseCase recordAlertFailure,
                                          DeadLetterPublishingRecoverer deadLetterRecoverer,
                                          AlertKafkaProperties properties) {
        long intervalMillis = properties.retryInterval().toMillis();
        RetryBackOffPolicy backOffPolicy = new RetryBackOffPolicy(intervalMillis, properties.defaultMaxRetries());
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new AlertFailureRecoverer(recordAlertFailure, deadLetterRecoverer),
                new FixedBackOff(intervalMillis, properties.defaultMaxRetries()));
        errorHandler.setBackOffFunction((record, exception) -> backOffPolicy.backOffFor(record));
        errorHandler.addNotRetryableExceptions(DomainValidationException.class, UnsupportedChannelTypeException.class);
        return errorHandler;
    }
}

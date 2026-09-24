package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RetryBackOffPolicyTest {

    private final RetryBackOffPolicy policy = new RetryBackOffPolicy(100, 3);

    @Test
    void shouldUseMaxRetriesConfiguredInTheChannel() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 0, "key", AlertMessages.withMaxRetries(5));

        assertThat(policy.maxRetriesOf(record)).isEqualTo(5);
    }

    @Test
    void shouldFallbackToDefaultWhenMessageCouldNotBeRead() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic", 0, 0, "key", null);

        assertThat(policy.maxRetriesOf(record)).isEqualTo(3);
    }
}

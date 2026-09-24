package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

class RetryBackOffPolicy {

    private final long intervalMillis;
    private final int defaultMaxRetries;

    RetryBackOffPolicy(long intervalMillis, int defaultMaxRetries) {
        this.intervalMillis = intervalMillis;
        this.defaultMaxRetries = defaultMaxRetries;
    }

    BackOff backOffFor(ConsumerRecord<?, ?> record) {
        return new FixedBackOff(intervalMillis, maxRetriesOf(record));
    }

    long maxRetriesOf(ConsumerRecord<?, ?> record) {
        if (record.value() instanceof AlertRequestedMessage message) {
            return message.maxRetries();
        }
        return defaultMaxRetries;
    }
}

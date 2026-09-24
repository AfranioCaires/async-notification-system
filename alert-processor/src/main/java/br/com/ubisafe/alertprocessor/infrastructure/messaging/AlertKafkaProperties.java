package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.kafka")
record AlertKafkaProperties(String deadLetterTopic, Duration retryInterval, int defaultMaxRetries) {
}

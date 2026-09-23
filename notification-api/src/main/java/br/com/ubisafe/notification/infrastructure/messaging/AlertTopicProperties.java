package br.com.ubisafe.notification.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.kafka.alerts-topic")
record AlertTopicProperties(String name, Duration publishTimeout) {
}

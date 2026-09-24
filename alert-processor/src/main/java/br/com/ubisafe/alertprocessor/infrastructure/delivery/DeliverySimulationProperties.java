package br.com.ubisafe.alertprocessor.infrastructure.delivery;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "app.delivery.simulation")
record DeliverySimulationProperties(Set<Long> failingClientIds) {

    DeliverySimulationProperties {
        failingClientIds = failingClientIds == null ? Set.of() : Set.copyOf(failingClientIds);
    }
}

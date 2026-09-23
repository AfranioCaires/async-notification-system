package br.com.ubisafe.alertprocessor.fixture;

import br.com.ubisafe.alertprocessor.application.alert.command.AlertData;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.Priority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public final class AlertFixtures {

    public static final Instant NOW = Instant.parse("2025-11-10T14:30:01Z");
    public static final UUID CORRELATION_ID = UUID.fromString("3f1c2a9e-8f7b-4b8e-9a51-2b9b1e0c7d11");

    private AlertFixtures() {
    }

    public static Clock fixedClock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }

    public static AlertData alertData(ChannelType channelType) {
        return new AlertData(CORRELATION_ID, channelType, 12345L, "Olá João, sua fatura está disponível.",
                Priority.HIGH, NOW);
    }

    public static Alert alert(ChannelType channelType) {
        return alertData(channelType).toAlert();
    }
}

package br.com.ubisafe.notification.fixture;

import br.com.ubisafe.notification.application.channel.command.ChannelData;
import br.com.ubisafe.notification.domain.channel.model.Channel;
import br.com.ubisafe.notification.domain.channel.model.ChannelConfig;
import br.com.ubisafe.notification.domain.channel.model.ChannelDefinition;
import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import br.com.ubisafe.notification.domain.channel.model.Template;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class ChannelFixtures {

    public static final Instant NOW = Instant.parse("2025-11-10T14:30:00Z");
    public static final String TEMPLATE = "Olá {{clientName}}, sua fatura de {{billingMonth}} está disponível.";

    private ChannelFixtures() {
    }

    public static Clock fixedClock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }

    public static ChannelDefinition definition() {
        return new ChannelDefinition("Fatura Disponível", ChannelType.EMAIL, new Template(TEMPLATE),
                new ChannelConfig(3, Priority.HIGH));
    }

    public static ChannelData data(boolean active) {
        return new ChannelData("Fatura Disponível", ChannelType.EMAIL, TEMPLATE, 3, Priority.HIGH, active);
    }

    public static Channel activeChannel() {
        return Channel.create(definition(), true, NOW);
    }

    public static Channel inactiveChannel() {
        return Channel.create(definition(), false, NOW);
    }
}

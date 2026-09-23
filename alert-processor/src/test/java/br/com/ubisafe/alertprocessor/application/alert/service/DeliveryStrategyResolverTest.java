package br.com.ubisafe.alertprocessor.application.alert.service;

import br.com.ubisafe.alertprocessor.application.alert.exception.UnsupportedChannelTypeException;
import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryStrategyResolverTest {

    @Test
    void shouldResolveStrategyByChannelType() {
        AlertDeliveryStrategy sms = strategy(ChannelType.SMS);
        DeliveryStrategyResolver resolver = new DeliveryStrategyResolver(List.of(strategy(ChannelType.EMAIL), sms));

        assertThat(resolver.resolve(ChannelType.SMS)).isSameAs(sms);
    }

    @Test
    void shouldRejectUnknownChannelType() {
        DeliveryStrategyResolver resolver = new DeliveryStrategyResolver(List.of(strategy(ChannelType.EMAIL)));

        assertThatThrownBy(() -> resolver.resolve(ChannelType.PUSH)).isInstanceOf(UnsupportedChannelTypeException.class);
    }

    @Test
    void shouldRejectDuplicatedStrategies() {
        assertThatThrownBy(() -> new DeliveryStrategyResolver(List.of(strategy(ChannelType.EMAIL), strategy(ChannelType.EMAIL))))
                .isInstanceOf(IllegalStateException.class);
    }

    private static AlertDeliveryStrategy strategy(ChannelType type) {
        return new AlertDeliveryStrategy() {
            @Override
            public ChannelType channelType() {
                return type;
            }

            @Override
            public void deliver(Alert alert) {
            }
        };
    }
}

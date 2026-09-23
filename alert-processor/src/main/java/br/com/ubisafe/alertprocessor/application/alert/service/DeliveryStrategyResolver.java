package br.com.ubisafe.alertprocessor.application.alert.service;

import br.com.ubisafe.alertprocessor.application.alert.exception.UnsupportedChannelTypeException;
import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class DeliveryStrategyResolver {

    private final Map<ChannelType, AlertDeliveryStrategy> strategies = new EnumMap<>(ChannelType.class);

    public DeliveryStrategyResolver(Collection<AlertDeliveryStrategy> strategies) {
        for (AlertDeliveryStrategy strategy : strategies) {
            AlertDeliveryStrategy previous = this.strategies.putIfAbsent(strategy.channelType(), strategy);
            if (previous != null) {
                throw new IllegalStateException("Estratégia duplicada para o tipo " + strategy.channelType());
            }
        }
    }

    public AlertDeliveryStrategy resolve(ChannelType channelType) {
        return Optional.ofNullable(strategies.get(channelType))
                .orElseThrow(() -> new UnsupportedChannelTypeException(channelType));
    }
}

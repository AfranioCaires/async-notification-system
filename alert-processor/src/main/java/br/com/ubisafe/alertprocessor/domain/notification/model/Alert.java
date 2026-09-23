package br.com.ubisafe.alertprocessor.domain.notification.model;

import br.com.ubisafe.alertprocessor.domain.shared.exception.DomainValidationException;

import java.time.Instant;
import java.util.Objects;

public record Alert(
        CorrelationId correlationId,
        ChannelType channelType,
        long clientId,
        String message,
        Priority priority,
        Instant requestedAt
) {

    public Alert {
        Objects.requireNonNull(correlationId, "correlationId");
        if (channelType == null) {
            throw new DomainValidationException("O tipo do canal é obrigatório");
        }
        if (clientId <= 0) {
            throw new DomainValidationException("clientId deve ser um número positivo");
        }
        if (message == null || message.isBlank()) {
            throw new DomainValidationException("A mensagem do alerta é obrigatória");
        }
        if (priority == null) {
            throw new DomainValidationException("A prioridade do alerta é obrigatória");
        }
        Objects.requireNonNull(requestedAt, "requestedAt");
    }
}

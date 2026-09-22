package br.com.ubisafe.notification.domain.channel.model;

import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;

public record ChannelConfig(int maxRetries, Priority priority) {

    public static final int MAX_RETRIES_LIMIT = 10;

    public ChannelConfig {
        if (maxRetries < 0 || maxRetries > MAX_RETRIES_LIMIT) {
            throw new DomainValidationException("maxRetries deve estar entre 0 e " + MAX_RETRIES_LIMIT);
        }
        if (priority == null) {
            throw new DomainValidationException("A prioridade do canal é obrigatória");
        }
    }
}

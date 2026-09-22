package br.com.ubisafe.notification.domain.alert.model;

import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;

public record ClientId(long value) {

    public ClientId {
        if (value <= 0) {
            throw new DomainValidationException("clientId deve ser um número positivo");
        }
    }
}

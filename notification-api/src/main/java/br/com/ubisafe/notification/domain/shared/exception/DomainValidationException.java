package br.com.ubisafe.notification.domain.shared.exception;

public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(message);
    }
}

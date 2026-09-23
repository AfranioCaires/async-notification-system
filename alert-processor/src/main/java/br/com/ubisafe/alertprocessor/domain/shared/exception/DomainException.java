package br.com.ubisafe.alertprocessor.domain.shared.exception;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}

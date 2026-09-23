package br.com.ubisafe.alertprocessor.application.alert.exception;

import br.com.ubisafe.alertprocessor.application.shared.exception.ApplicationException;

import java.util.UUID;

public class AlertNotFoundException extends ApplicationException {

    public AlertNotFoundException(UUID correlationId) {
        super("Nenhum evento encontrado para o correlationId " + correlationId);
    }
}

package br.com.ubisafe.notification.application.port;

import br.com.ubisafe.notification.application.shared.exception.ApplicationException;

public class AlertPublicationException extends ApplicationException {

    public AlertPublicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

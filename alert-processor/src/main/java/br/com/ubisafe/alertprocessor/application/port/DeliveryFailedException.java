package br.com.ubisafe.alertprocessor.application.port;

import br.com.ubisafe.alertprocessor.application.shared.exception.ApplicationException;

public class DeliveryFailedException extends ApplicationException {

    public DeliveryFailedException(String message) {
        super(message);
    }

    public DeliveryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

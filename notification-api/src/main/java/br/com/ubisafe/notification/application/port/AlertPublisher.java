package br.com.ubisafe.notification.application.port;

import br.com.ubisafe.notification.domain.alert.event.AlertRequested;

public interface AlertPublisher {

    void publish(AlertRequested alert);
}

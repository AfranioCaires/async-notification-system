package br.com.ubisafe.alertprocessor.domain.notification.repository;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;

public interface NotificationEventRepository {

    boolean append(NotificationEvent event);

    NotificationHistory historyOf(CorrelationId correlationId);
}

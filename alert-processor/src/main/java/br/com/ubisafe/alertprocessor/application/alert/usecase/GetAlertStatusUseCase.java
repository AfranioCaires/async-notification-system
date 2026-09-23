package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.dto.AlertStatusView;
import br.com.ubisafe.alertprocessor.application.alert.exception.AlertNotFoundException;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;

import java.util.UUID;

public class GetAlertStatusUseCase {

    private final NotificationEventRepository repository;

    public GetAlertStatusUseCase(NotificationEventRepository repository) {
        this.repository = repository;
    }

    public AlertStatusView execute(UUID correlationId) {
        NotificationHistory history = repository.historyOf(new CorrelationId(correlationId));
        if (history.isEmpty()) {
            throw new AlertNotFoundException(correlationId);
        }
        return AlertStatusView.from(history);
    }
}

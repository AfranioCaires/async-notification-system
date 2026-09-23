package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.command.RecordAlertFailureCommand;
import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;

import java.time.Clock;

public class RecordAlertFailureUseCase {

    private final NotificationEventRepository repository;
    private final Clock clock;

    public RecordAlertFailureUseCase(NotificationEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void execute(RecordAlertFailureCommand command) {
        Alert alert = command.alert().toAlert();
        NotificationHistory history = repository.historyOf(alert.correlationId());
        if (history.isConcluded()) {
            return;
        }
        if (!history.hasReached(NotificationStatus.RECEBIDO)) {
            repository.append(NotificationEvent.received(alert, clock.instant()));
        }
        repository.append(NotificationEvent.failed(alert, command.reason(), clock.instant()));
    }
}

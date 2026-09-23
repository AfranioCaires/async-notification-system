package br.com.ubisafe.alertprocessor.application.alert.usecase;

import br.com.ubisafe.alertprocessor.application.alert.command.ProcessAlertCommand;
import br.com.ubisafe.alertprocessor.application.alert.dto.ProcessingOutcome;
import br.com.ubisafe.alertprocessor.application.alert.service.DeliveryStrategyResolver;
import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;

import java.time.Clock;

public class ProcessAlertUseCase {

    private final NotificationEventRepository repository;
    private final DeliveryStrategyResolver strategyResolver;
    private final Clock clock;

    public ProcessAlertUseCase(NotificationEventRepository repository, DeliveryStrategyResolver strategyResolver,
                               Clock clock) {
        this.repository = repository;
        this.strategyResolver = strategyResolver;
        this.clock = clock;
    }

    public ProcessingOutcome execute(ProcessAlertCommand command) {
        Alert alert = command.alert().toAlert();
        NotificationHistory history = repository.historyOf(alert.correlationId());
        if (history.isConcluded()) {
            return ProcessingOutcome.ALREADY_CONCLUDED;
        }
        if (!history.hasReached(NotificationStatus.RECEBIDO)) {
            repository.append(NotificationEvent.received(alert, clock.instant()));
        }
        AlertDeliveryStrategy strategy = strategyResolver.resolve(alert.channelType());
        if (!history.hasReached(NotificationStatus.PROCESSANDO)) {
            repository.append(NotificationEvent.processing(alert, clock.instant()));
        }
        strategy.deliver(alert);
        repository.append(NotificationEvent.processed(alert, clock.instant()));
        return ProcessingOutcome.PROCESSED;
    }
}

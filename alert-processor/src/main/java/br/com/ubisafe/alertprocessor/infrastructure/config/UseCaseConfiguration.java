package br.com.ubisafe.alertprocessor.infrastructure.config;

import br.com.ubisafe.alertprocessor.application.alert.service.DeliveryStrategyResolver;
import br.com.ubisafe.alertprocessor.application.alert.usecase.GetAlertStatusUseCase;
import br.com.ubisafe.alertprocessor.application.alert.usecase.ProcessAlertUseCase;
import br.com.ubisafe.alertprocessor.application.alert.usecase.RecordAlertFailureUseCase;
import br.com.ubisafe.alertprocessor.application.port.AlertDeliveryStrategy;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class UseCaseConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    DeliveryStrategyResolver deliveryStrategyResolver(List<AlertDeliveryStrategy> strategies) {
        return new DeliveryStrategyResolver(strategies);
    }

    @Bean
    ProcessAlertUseCase processAlertUseCase(NotificationEventRepository repository,
                                            DeliveryStrategyResolver resolver, Clock clock) {
        return new ProcessAlertUseCase(repository, resolver, clock);
    }

    @Bean
    RecordAlertFailureUseCase recordAlertFailureUseCase(NotificationEventRepository repository, Clock clock) {
        return new RecordAlertFailureUseCase(repository, clock);
    }

    @Bean
    GetAlertStatusUseCase getAlertStatusUseCase(NotificationEventRepository repository) {
        return new GetAlertStatusUseCase(repository);
    }
}

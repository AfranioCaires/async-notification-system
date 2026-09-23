package br.com.ubisafe.alertprocessor.domain.notification;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.Alert;
import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import br.com.ubisafe.alertprocessor.domain.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.alert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationHistoryTest {

    private final Alert alert = alert(ChannelType.EMAIL);

    @Test
    void shouldDeriveCurrentStatusFromLatestEvent() {
        NotificationHistory history = new NotificationHistory(new CorrelationId(CORRELATION_ID), List.of(
                NotificationEvent.processed(alert, NOW.plusSeconds(2)),
                NotificationEvent.received(alert, NOW),
                NotificationEvent.processing(alert, NOW.plusSeconds(1))
        ));

        assertThat(history.currentStatus()).contains(NotificationStatus.PROCESSADO);
        assertThat(history.events()).extracting(NotificationEvent::status).containsExactly(
                NotificationStatus.RECEBIDO, NotificationStatus.PROCESSANDO, NotificationStatus.PROCESSADO);
        assertThat(history.isConcluded()).isTrue();
    }

    @Test
    void shouldUseLifecycleOrderWhenTimestampsTie() {
        NotificationHistory history = new NotificationHistory(new CorrelationId(CORRELATION_ID), List.of(
                NotificationEvent.processing(alert, NOW),
                NotificationEvent.received(alert, NOW)
        ));

        assertThat(history.currentStatus()).contains(NotificationStatus.PROCESSANDO);
        assertThat(history.isConcluded()).isFalse();
    }

    @Test
    void shouldBeEmptyWithoutEvents() {
        NotificationHistory history = new NotificationHistory(new CorrelationId(CORRELATION_ID), List.of());

        assertThat(history.isEmpty()).isTrue();
        assertThat(history.currentStatus()).isEmpty();
    }

    @Test
    void shouldDescribeStrategyInProcessingEvent() {
        assertThat(NotificationEvent.processing(alert, NOW).detail())
                .isEqualTo("Iniciando processamento via estratégia EMAIL");
    }

    @Test
    void shouldTruncateLongFailureDetail() {
        NotificationEvent event = NotificationEvent.failed(alert, "x".repeat(2000), NOW);

        assertThat(event.detail()).hasSize(NotificationEvent.DETAIL_MAX_LENGTH);
    }

    @Test
    void shouldRejectAlertWithoutMessage() {
        assertThatThrownBy(() -> new Alert(new CorrelationId(CORRELATION_ID), ChannelType.SMS, 1L, " ",
                alert.priority(), NOW)).isInstanceOf(DomainValidationException.class);
    }
}

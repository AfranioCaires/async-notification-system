package br.com.ubisafe.alertprocessor.infrastructure.messaging;

import br.com.ubisafe.alertprocessor.domain.notification.model.ChannelType;
import br.com.ubisafe.alertprocessor.domain.notification.model.Priority;

import java.util.UUID;

import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.CORRELATION_ID;
import static br.com.ubisafe.alertprocessor.fixture.AlertFixtures.NOW;

final class AlertMessages {

    private AlertMessages() {
    }

    static AlertRequestedMessage withMaxRetries(int maxRetries) {
        return new AlertRequestedMessage(CORRELATION_ID, UUID.randomUUID(), ChannelType.EMAIL, 12345L,
                "Olá João", Priority.HIGH, maxRetries, NOW);
    }
}

package br.com.ubisafe.alertprocessor.infrastructure.persistence;

import br.com.ubisafe.alertprocessor.domain.notification.event.NotificationEvent;
import br.com.ubisafe.alertprocessor.domain.notification.model.CorrelationId;
import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationHistory;
import br.com.ubisafe.alertprocessor.domain.notification.repository.NotificationEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
class JpaNotificationEventRepository implements NotificationEventRepository {

    private final SpringDataNotificationEventRepository springData;

    JpaNotificationEventRepository(SpringDataNotificationEventRepository springData) {
        this.springData = springData;
    }

    @Override
    public boolean append(NotificationEvent event) {
        try {
            springData.saveAndFlush(NotificationEventEntityMapper.toEntity(event));
            return true;
        } catch (DataIntegrityViolationException exception) {
            if (springData.existsByCorrelationIdAndStatus(event.correlationId().toString(), event.status())) {
                return false;
            }
            throw exception;
        }
    }

    @Override
    public NotificationHistory historyOf(CorrelationId correlationId) {
        return new NotificationHistory(
                correlationId,
                springData.findByCorrelationIdOrderByOccurredAtAsc(correlationId.toString()).stream()
                        .map(NotificationEventEntityMapper::toDomain)
                        .toList()
        );
    }
}

package br.com.ubisafe.alertprocessor.infrastructure.persistence;

import br.com.ubisafe.alertprocessor.domain.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataNotificationEventRepository extends JpaRepository<NotificationEventJpaEntity, String> {

    List<NotificationEventJpaEntity> findByCorrelationIdOrderByOccurredAtAsc(String correlationId);

    boolean existsByCorrelationIdAndStatus(String correlationId, NotificationStatus status);
}

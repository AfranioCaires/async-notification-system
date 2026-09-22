package br.com.ubisafe.notification.infrastructure.persistence;

import br.com.ubisafe.notification.domain.channel.model.ChannelType;
import br.com.ubisafe.notification.domain.channel.model.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.time.Instant;

@Entity
@Table(name = "channels")
class ChannelJpaEntity implements Persistable<String> {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private ChannelType type;

    @Column(name = "template", nullable = false, length = 2000)
    private String template;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 16)
    private Priority priority;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Transient
    private boolean newEntity;

    protected ChannelJpaEntity() {
    }

    ChannelJpaEntity(String id, String name, ChannelType type, String template, int maxRetries, Priority priority,
                     boolean active, Instant createdAt, Instant updatedAt, Instant deletedAt, boolean newEntity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.template = template;
        this.maxRetries = maxRetries;
        this.priority = priority;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.newEntity = newEntity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    String getName() {
        return name;
    }

    ChannelType getType() {
        return type;
    }

    String getTemplate() {
        return template;
    }

    int getMaxRetries() {
        return maxRetries;
    }

    Priority getPriority() {
        return priority;
    }

    boolean isActive() {
        return active;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    Instant getDeletedAt() {
        return deletedAt;
    }
}

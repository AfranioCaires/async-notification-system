CREATE TABLE notification_events
(
    id             VARCHAR(36)   NOT NULL,
    correlation_id VARCHAR(36)   NOT NULL,
    channel_type   VARCHAR(16)   NOT NULL,
    client_id      BIGINT        NOT NULL,
    status         VARCHAR(16)   NOT NULL,
    detail         VARCHAR(1000) NOT NULL,
    occurred_at    DATETIME(6)   NOT NULL,
    CONSTRAINT pk_notification_events PRIMARY KEY (id),
    CONSTRAINT uk_notification_events_correlation_status UNIQUE (correlation_id, status),
    CONSTRAINT ck_notification_events_status CHECK (status IN ('RECEBIDO', 'PROCESSANDO', 'PROCESSADO', 'FALHA'))
);

CREATE INDEX idx_notification_events_correlation_time ON notification_events (correlation_id, occurred_at);

CREATE TRIGGER trg_notification_events_no_update
    BEFORE UPDATE
    ON notification_events
    FOR EACH ROW
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'notification_events is append-only';

CREATE TRIGGER trg_notification_events_no_delete
    BEFORE DELETE
    ON notification_events
    FOR EACH ROW
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'notification_events is append-only';

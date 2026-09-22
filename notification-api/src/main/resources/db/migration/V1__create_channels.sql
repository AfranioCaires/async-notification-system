CREATE TABLE channels
(
    id          VARCHAR(36)   NOT NULL,
    name        VARCHAR(120)  NOT NULL,
    type        VARCHAR(16)   NOT NULL,
    template    VARCHAR(2000) NOT NULL,
    max_retries INT           NOT NULL,
    priority    VARCHAR(16)   NOT NULL,
    active      BOOLEAN       NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,
    deleted_at  DATETIME(6)   NULL,
    live_marker TINYINT GENERATED ALWAYS AS (IF(deleted_at IS NULL, 1, NULL)) STORED,
    CONSTRAINT pk_channels PRIMARY KEY (id),
    CONSTRAINT uk_channels_name_type_live UNIQUE (name, type, live_marker),
    CONSTRAINT ck_channels_type CHECK (type IN ('EMAIL', 'SMS', 'PUSH')),
    CONSTRAINT ck_channels_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT ck_channels_max_retries CHECK (max_retries BETWEEN 0 AND 10)
);

CREATE INDEX idx_channels_deleted_created ON channels (deleted_at, created_at);

CREATE TABLE outbox_events
(
    id             UUID PRIMARY KEY,
    aggregate_id   UUID         NOT NULL,
    aggregate_type VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        JSONB        NOT NULL,
    occurred_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed      BOOLEAN      NOT NULL DEFAULT FALSE,
    status         VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    last_error     VARCHAR(1000),
    retry_count    INT          NOT NULL DEFAULT 0,
    next_retry_at  TIMESTAMP
);

CREATE INDEX idx_outbox_status ON outbox_events (status) WHERE processed = false;
CREATE INDEX idx_outbox_occurred_at ON outbox_events (occurred_at);
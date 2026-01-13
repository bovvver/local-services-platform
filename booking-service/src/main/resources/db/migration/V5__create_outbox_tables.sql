CREATE TABLE outbox_events (
    id            UUID PRIMARY KEY,
    aggregate_id  UUID           NOT NULL,
    aggregate_type VARCHAR(50)   NOT NULL,
    event_type     VARCHAR(100)  NOT NULL,
    payload        JSONB         NOT NULL,
    occurred_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed      BOOLEAN       NOT NULL DEFAULT FALSE
);

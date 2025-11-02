CREATE TABLE negotiations
(
    id         UUID PRIMARY KEY,
    booking_id UUID        NOT NULL,
    status     VARCHAR(20) NOT NULL,
    started_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL
);

CREATE TABLE negotiation_positions
(
    id              UUID PRIMARY KEY,
    negotiation_id  UUID           NOT NULL,
    proposed_salary DECIMAL(10, 2) NOT NULL,
    proposed_by     VARCHAR(20)    NOT NULL,
    proposed_at     TIMESTAMP      NOT NULL,
    CONSTRAINT fk_negotiation FOREIGN KEY (negotiation_id)
        REFERENCES negotiations (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_salary_non_negative CHECK (proposed_salary >= 0)
);

CREATE INDEX idx_negotiation_position__negotiation_id ON negotiation_positions (negotiation_id);
-- =============================================================================
-- V3: Replace verification_requests with a single verification record per user
-- =============================================================================
-- The previous VerificationRequest aggregate (one row per type per user) is
-- replaced by a single Verification aggregate (one row per user) that holds
-- both statuses as columns. This better reflects the domain: a user has one
-- current identity verification state and one professional licence state.
-- =============================================================================

DROP TABLE IF EXISTS verification_requests;

CREATE TABLE verification (
    user_id          UUID        NOT NULL PRIMARY KEY,
    identity_status  VARCHAR(20) NOT NULL,
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_verification_user_id ON verification (user_id);

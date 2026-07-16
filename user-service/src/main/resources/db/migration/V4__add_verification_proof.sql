-- =============================================================================
-- V4: Add verification proof columns to verification table
-- =============================================================================

ALTER TABLE verification ADD COLUMN proof_url VARCHAR(255);
ALTER TABLE verification ADD COLUMN proof_uploaded_at TIMESTAMP;

ALTER TABLE users DROP COLUMN status;
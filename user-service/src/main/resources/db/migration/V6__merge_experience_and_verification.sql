-- =============================================================================
-- V6: Merge experience_snapshot into reputation and verification into users
-- =============================================================================

-- ── 1. verification into users ────────────────────────────────────────────────
ALTER TABLE users ADD COLUMN identity_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
ALTER TABLE users ADD COLUMN proof_url VARCHAR(255);
ALTER TABLE users ADD COLUMN proof_uploaded_at TIMESTAMP;

-- Migrate existing verification data
UPDATE users u
SET identity_status = v.identity_status,
    proof_url = v.proof_url,
    proof_uploaded_at = v.proof_uploaded_at
FROM verification v
WHERE u.id = v.user_id;

-- Drop legacy verification table
DROP TABLE IF EXISTS verification CASCADE;

-- ── 2. experience_snapshot into reputation ────────────────────────────────────
ALTER TABLE reputation ADD COLUMN experience_level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER';
ALTER TABLE reputation ADD COLUMN experience_score INTEGER NOT NULL DEFAULT 0;

-- Migrate existing experience snapshot data
UPDATE reputation r
SET experience_level = e.level,
    experience_score = e.score
FROM experience_snapshot e
WHERE r.user_id = e.user_id;

-- Drop legacy experience_snapshot table
DROP TABLE IF EXISTS experience_snapshot CASCADE;

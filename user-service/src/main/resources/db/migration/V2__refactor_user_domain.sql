-- =============================================================================
-- V2: User Domain Refactor
-- =============================================================================
-- Drops legacy element-collection tables and columns from V1.
-- Creates new aggregate tables: provider_profiles, provider_categories,
-- verification_requests (superseded by V3), reputation, badges, experience_snapshot
-- (superseded by V6).
-- =============================================================================

-- ── 1. Drop legacy element-collection tables (no longer needed) ──────────────
DROP TABLE IF EXISTS user_service_categories;
DROP TABLE IF EXISTS user_award_tags;
DROP TABLE IF EXISTS user_my_offers;
DROP TABLE IF EXISTS user_assigned_offers;
DROP TABLE IF EXISTS user_sent_bookings;

-- ── 2. Drop legacy columns from users ─────────────────────────────────────────
ALTER TABLE users
    DROP COLUMN IF EXISTS city,
    DROP COLUMN IF EXISTS country,
    DROP COLUMN IF EXISTS experience_level;

-- ── 3. Add index on users.email (high-frequency query column) ─────────────────
CREATE INDEX idx_users_email ON users (email);

-- ── 4. provider_profiles ──────────────────────────────────────────────────────
CREATE TABLE provider_profiles
(
    id      UUID PRIMARY KEY,
    user_id UUID         NOT NULL,
    bio     TEXT,
    city    VARCHAR(100),
    country CHAR(2),
    CONSTRAINT fk_provider_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_provider_profiles_user UNIQUE (user_id)
);

CREATE INDEX idx_provider_profiles_user_id ON provider_profiles (user_id);

-- ── 5. provider_categories (ElementCollection for ProviderProfile.categories) ─
CREATE TABLE provider_categories
(
    profile_id UUID        NOT NULL,
    category   VARCHAR(50) NOT NULL,
    PRIMARY KEY (profile_id, category),
    CONSTRAINT fk_provider_categories_profile FOREIGN KEY (profile_id) REFERENCES provider_profiles (id) ON DELETE CASCADE
);

-- ── 6. verification_requests ──────────────────────────────────────────────────
CREATE TABLE verification_requests
(
    id      UUID PRIMARY KEY,
    user_id UUID        NOT NULL,
    type    VARCHAR(30) NOT NULL,
    status  VARCHAR(20) NOT NULL,
    CONSTRAINT fk_verification_requests_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_verification_requests_user_id ON verification_requests (user_id);

-- ── 7. reputation ─────────────────────────────────────────────────────────────
CREATE TABLE reputation
(
    user_id             UUID    NOT NULL PRIMARY KEY,
    average_rating      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    completed_bookings  INTEGER NOT NULL DEFAULT 0,
    cancelled_bookings  INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_reputation_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_reputation_user_id ON reputation (user_id);

-- ── 8. badges ─────────────────────────────────────────────────────────────────
CREATE TABLE badges
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    badge_type VARCHAR(30) NOT NULL,
    awarded_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ,
    CONSTRAINT fk_badges_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_badges_user_id ON badges (user_id);

-- ── 9. experience_snapshot ────────────────────────────────────────────────────
CREATE TABLE experience_snapshot
(
    user_id UUID        NOT NULL PRIMARY KEY,
    level   VARCHAR(20) NOT NULL,
    score   INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT fk_experience_snapshot_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_experience_snapshot_user_id ON experience_snapshot (user_id);
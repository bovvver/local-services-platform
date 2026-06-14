ALTER TABLE bookings
    ADD COLUMN expires_at TIMESTAMP;

UPDATE bookings
SET expires_at = created_at + INTERVAL '14 days';

ALTER TABLE bookings
    ALTER COLUMN expires_at SET NOT NULL;
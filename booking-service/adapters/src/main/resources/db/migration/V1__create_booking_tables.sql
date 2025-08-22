CREATE TABLE bookings
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    offer_id   UUID        NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL,

    CONSTRAINT bookings_user_offer_unique UNIQUE (user_id, offer_id)
);
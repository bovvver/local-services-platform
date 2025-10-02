CREATE TABLE booking_drafts
(
    booking_id UUID        PRIMARY KEY,
    offer_id   UUID        NOT NULL,
    user_id    UUID        NOT NULL,
    salary     DECIMAL(10,2),
    created_at TIMESTAMP   NOT NULL,

    CONSTRAINT booking_drafts_booking_offer_unique UNIQUE (booking_id, offer_id)
);
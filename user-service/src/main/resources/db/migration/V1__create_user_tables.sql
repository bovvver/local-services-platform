CREATE TABLE users
(
    id               UUID PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    city             VARCHAR(100),
    country          CHAR(2),
    experience_level VARCHAR(20) NOT NULL,
    status           VARCHAR(20) NOT NULL
);

CREATE TABLE user_service_categories
(
    user_id  UUID        NOT NULL,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, category),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_award_tags
(
    user_id UUID        NOT NULL,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, tag),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_my_offers
(
    user_id  UUID NOT NULL,
    offer_id UUID NOT NULL,
    PRIMARY KEY (user_id, offer_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_assigned_offers
(
    user_id  UUID NOT NULL,
    offer_id UUID NOT NULL,
    PRIMARY KEY (user_id, offer_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_sent_bookings
(
    user_id    UUID NOT NULL,
    booking_id UUID NOT NULL,
    PRIMARY KEY (user_id, booking_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
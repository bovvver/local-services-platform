ALTER TABLE bookings
    ADD COLUMN final_salary DECIMAL(10, 2),
    ADD COLUMN negotiation_id UUID;
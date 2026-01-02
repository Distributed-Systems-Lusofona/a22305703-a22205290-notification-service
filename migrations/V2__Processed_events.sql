CREATE TABLE IF NOT EXISTS processed_events (
                                                id BIGSERIAL PRIMARY KEY,
                                                reservation_id BIGINT NOT NULL,
                                                event_type VARCHAR(80) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_processed_res_event UNIQUE (reservation_id, event_type)
    );

CREATE INDEX IF NOT EXISTS idx_processed_events_reservation_id
    ON processed_events(reservation_id);

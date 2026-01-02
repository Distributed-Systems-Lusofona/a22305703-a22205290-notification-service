CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               reservation_id BIGINT NOT NULL,
                               event_type VARCHAR(64) NOT NULL,
                               recipient VARCHAR(255) NOT NULL,
                               status VARCHAR(32) NOT NULL,
                               trace_id VARCHAR(64) NOT NULL,
                               related_event_type VARCHAR(64),
                               error_message TEXT,
                               sent_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_reservation_id ON notifications(reservation_id);
CREATE INDEX idx_notifications_event_type ON notifications(event_type);

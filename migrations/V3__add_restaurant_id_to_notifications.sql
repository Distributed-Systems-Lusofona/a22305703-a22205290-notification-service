ALTER TABLE notifications
    ADD COLUMN restaurant_id BIGINT NOT NULL;

CREATE INDEX idx_notifications_restaurant_id ON notifications(restaurant_id);

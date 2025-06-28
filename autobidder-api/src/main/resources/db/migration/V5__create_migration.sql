CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID,
    type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    pushed BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_date TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
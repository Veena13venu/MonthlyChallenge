-- V7: Device FCM tokens for push notifications
-- One row per user per device; a user can have multiple devices
CREATE TABLE device_tokens (
    id          UUID        NOT NULL PRIMARY KEY,
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token   TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_device_token UNIQUE (fcm_token)
);

CREATE INDEX idx_device_tokens_user ON device_tokens (user_id);

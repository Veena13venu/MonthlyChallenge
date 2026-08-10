-- V5: Streaks — one row per user, updated nightly
CREATE TABLE streaks (
    id                UUID NOT NULL PRIMARY KEY,
    user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    current_streak    INT  NOT NULL DEFAULT 0,
    longest_streak    INT  NOT NULL DEFAULT 0,
    last_success_date DATE,

    CONSTRAINT uq_streak_user UNIQUE (user_id)
);

CREATE INDEX idx_streaks_user ON streaks (user_id);

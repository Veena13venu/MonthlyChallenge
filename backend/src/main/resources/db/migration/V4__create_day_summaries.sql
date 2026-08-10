-- V4: Day summaries — one row per user per calendar day (computed at end-of-day)
CREATE TABLE day_summaries (
    id                  UUID             NOT NULL PRIMARY KEY,
    user_id             UUID             NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date                DATE             NOT NULL,
    total_points        DOUBLE PRECISION NOT NULL DEFAULT 0,
    minimum_threshold   DOUBLE PRECISION NOT NULL DEFAULT 1,
    result              VARCHAR(10)      NOT NULL,   -- SUCCESS | PARTIAL | MISSED

    CONSTRAINT uq_day_summary_user_date UNIQUE (user_id, date)
);

CREATE INDEX idx_day_summaries_user_date ON day_summaries (user_id, date);

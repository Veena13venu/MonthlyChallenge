-- V2: Challenges table
CREATE TABLE challenges (
    id               UUID         NOT NULL PRIMARY KEY,
    owner_id         UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title            VARCHAR(150) NOT NULL,
    description      VARCHAR(500),
    category         VARCHAR(30)  NOT NULL,
    frequency        VARCHAR(20)  NOT NULL,   -- DAILY | WEEKLY | MONTHLY
    target_value     VARCHAR(50),              -- e.g. "3.0:Litres"
    month            VARCHAR(7)   NOT NULL,    -- e.g. "2026-07"
    visibility       VARCHAR(10)  NOT NULL DEFAULT 'SHARED',
    reminder_hour    SMALLINT,
    reminder_minute  SMALLINT,
    weekly_due_days  VARCHAR(100),             -- e.g. "MONDAY,WEDNESDAY"
    monthly_due_day  SMALLINT,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_challenges_owner_month        ON challenges (owner_id, month) WHERE active = TRUE;
CREATE INDEX idx_challenges_reminder           ON challenges (reminder_hour, reminder_minute) WHERE active = TRUE;

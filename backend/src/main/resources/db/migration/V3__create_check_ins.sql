-- V3: Check-ins table
-- One row per user × challenge × date (unique constraint enforces this)
CREATE TABLE check_ins (
    id            UUID        NOT NULL PRIMARY KEY,
    challenge_id  UUID        NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    user_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date          DATE        NOT NULL,
    status        VARCHAR(20) NOT NULL,   -- COMPLETED | HALF_COMPLETED | MISSED
    actual_value  DOUBLE PRECISION,       -- optional actual value for measurable targets
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_checkin_user_challenge_date UNIQUE (user_id, challenge_id, date)
);

CREATE INDEX idx_check_ins_user_date         ON check_ins (user_id, date);
CREATE INDEX idx_check_ins_challenge_date    ON check_ins (challenge_id, date);

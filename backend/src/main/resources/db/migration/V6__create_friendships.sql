-- V6: Friendships — mutual request/accept model
CREATE TABLE friendships (
    id            UUID        NOT NULL PRIMARY KEY,
    requester_id  UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    addressee_id  UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING | ACCEPTED | DECLINED
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Prevent duplicate requests in either direction
CREATE UNIQUE INDEX uq_friendship_pair ON friendships (
    LEAST(requester_id::text, addressee_id::text),
    GREATEST(requester_id::text, addressee_id::text)
) WHERE status IN ('PENDING', 'ACCEPTED');

CREATE INDEX idx_friendships_requester ON friendships (requester_id, status);
CREATE INDEX idx_friendships_addressee ON friendships (addressee_id, status);

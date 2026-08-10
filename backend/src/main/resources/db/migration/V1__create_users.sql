-- V1: Users table
CREATE TABLE users (
    id                    UUID         NOT NULL PRIMARY KEY,
    keycloak_id           VARCHAR(255) NOT NULL,
    username              VARCHAR(50)  NOT NULL,
    display_name          VARCHAR(100),
    email                 VARCHAR(255) NOT NULL,
    profile_photo_url     VARCHAR(500),
    minimum_daily_target  VARCHAR(20)  NOT NULL DEFAULT '1.0:false',
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_keycloak_id UNIQUE (keycloak_id),
    CONSTRAINT uq_users_username    UNIQUE (username)
);

CREATE INDEX idx_users_keycloak_id ON users (keycloak_id);
CREATE INDEX idx_users_username    ON users (LOWER(username));

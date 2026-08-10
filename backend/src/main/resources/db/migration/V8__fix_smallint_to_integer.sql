-- V8: Convert SMALLINT columns to INTEGER to match Hibernate's type expectations
ALTER TABLE challenges
    ALTER COLUMN reminder_hour   TYPE INTEGER,
    ALTER COLUMN reminder_minute TYPE INTEGER,
    ALTER COLUMN monthly_due_day TYPE INTEGER;

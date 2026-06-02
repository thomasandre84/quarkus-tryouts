-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

-- DDL for lock table used by LockService
-- Adjust types for your RDBMS; this example targets Postgres (timestamptz)
CREATE TABLE IF NOT EXISTS sftp_locks (
	resource TEXT PRIMARY KEY,
	owner TEXT NOT NULL,
	expires_at TIMESTAMPTZ NOT NULL
);

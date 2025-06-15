CREATE TABLE IF NOT EXISTS Users (
    id UUID PRIMARY KEY,
    keycloack_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    last_synced_at TIMESTAMP WITHOUT TIME ZONE,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);
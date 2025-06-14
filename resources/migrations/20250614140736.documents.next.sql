CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    retrieved_at TIMESTAMP,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP
);

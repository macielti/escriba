CREATE TABLE IF NOT EXISTS commands (
    id UUID PRIMARY KEY,
    index INTEGER NOT NULL,
    document_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    scan_lines INTEGER
);

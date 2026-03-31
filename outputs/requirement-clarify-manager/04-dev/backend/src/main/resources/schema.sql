-- Requirement Clarify Manager Schema

CREATE TABLE IF NOT EXISTS requirements (
    id SERIAL PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    requirement_no VARCHAR(255) NOT NULL UNIQUE,
    version VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    original_content TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS requirement_history (
    id SERIAL PRIMARY KEY,
    requirement_id INTEGER NOT NULL,
    from_status VARCHAR(50) NOT NULL,
    to_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reason TEXT,
    FOREIGN KEY (requirement_id) REFERENCES requirements(id)
);

CREATE TABLE IF NOT EXISTS data_dictionaries (
    id SERIAL PRIMARY KEY,
    requirement_id INTEGER NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    korean_name VARCHAR(255) NOT NULL,
    english_name VARCHAR(255),
    data_type VARCHAR(100),
    data_length INTEGER,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES requirements(id)
);

CREATE TABLE IF NOT EXISTS analysis_results (
    id SERIAL PRIMARY KEY,
    requirement_id INTEGER NOT NULL UNIQUE,
    actors TEXT,       -- JSON array
    commands TEXT,     -- JSON array
    domain_events TEXT, -- JSON array
    aggregates TEXT,    -- JSON array
    external_systems TEXT, -- JSON array
    diagram_source TEXT,   -- Mermaid.js source
    status VARCHAR(50) NOT NULL, -- PROCESSING, COMPLETED, FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requirement_id) REFERENCES requirements(id)
);

CREATE TABLE IF NOT EXISTS todos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(500) NOT NULL,
    description VARCHAR(2000),
    completed BOOLEAN NOT NULL DEFAULT 0,
    priority VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    assigned_to VARCHAR(100),
    tags VARCHAR(50),
    estimated_hours INTEGER NOT NULL DEFAULT 0
);

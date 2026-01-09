-- SQL schema for Task Reminder Application
-- H2 Database Schema

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    due_timestamp BIGINT NOT NULL,
    email VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at BIGINT NOT NULL,
    completed_at BIGINT
);

-- Create index on status for faster queries
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);

-- Create index on due_timestamp for scheduling queries
CREATE INDEX IF NOT EXISTS idx_tasks_due_timestamp ON tasks(due_timestamp);


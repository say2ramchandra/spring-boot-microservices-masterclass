-- Schema for Connection Pool Demo

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO tasks (title, description, status) VALUES 
('Task 1', 'First task', 'PENDING'),
('Task 2', 'Second task', 'IN_PROGRESS'),
('Task 3', 'Third task', 'COMPLETED');

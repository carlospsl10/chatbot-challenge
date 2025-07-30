-- Database initialization script for Order Status Chatbot
-- This script creates sample data for testing purposes

-- Create customers table if not exists
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table if not exists
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2),
    shipping_address TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create conversations table if not exists
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    session_id VARCHAR(255),
    message TEXT NOT NULL,
    response TEXT,
    intent VARCHAR(100),
    confidence DECIMAL(3,2),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Insert sample customers
INSERT INTO customers (email, password, first_name, last_name, phone) VALUES
('john.doe@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Doe', '+1-555-0101'),
('jane.smith@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Smith', '+1-555-0102'),
('bob.wilson@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Bob', 'Wilson', '+1-555-0103')
ON CONFLICT (email) DO NOTHING;

-- Insert sample orders
INSERT INTO orders (order_number, customer_id, status, total_amount, shipping_address) VALUES
('ORD-001', 1, 'DELIVERED', 199.99, '123 Main St, New York, NY 10001'),
('ORD-002', 1, 'SHIPPED', 149.50, '456 Oak Ave, Los Angeles, CA 90210'),
('ORD-003', 2, 'PROCESSING', 89.99, '789 Pine St, Chicago, IL 60601'),
('ORD-004', 2, 'DELIVERED', 299.99, '321 Elm St, Houston, TX 77001'),
('ORD-005', 3, 'SHIPPED', 179.99, '654 Maple Dr, Phoenix, AZ 85001')
ON CONFLICT (order_number) DO NOTHING;

-- Insert sample conversations
INSERT INTO conversations (customer_id, session_id, message, response, intent, confidence) VALUES
(1, 'session-001', 'Where is my order ORD-001?', 'Your order ORD-001 has been delivered to 123 Main St, New York, NY 10001', 'ORDER_STATUS', 0.95),
(1, 'session-001', 'What is the status of ORD-002?', 'Your order ORD-002 is currently shipped and on its way to 456 Oak Ave, Los Angeles, CA 90210', 'ORDER_STATUS', 0.92),
(2, 'session-002', 'Show me my order history', 'Here are your recent orders: ORD-003 (Processing), ORD-004 (Delivered)', 'ORDER_HISTORY', 0.88)
ON CONFLICT DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_conversations_customer_id ON conversations(customer_id);
CREATE INDEX IF NOT EXISTS idx_conversations_timestamp ON conversations(timestamp);

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO chatbot;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO chatbot; 
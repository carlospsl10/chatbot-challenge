-- =============================================================================
-- COMPREHENSIVE DATABASE INITIALIZATION SCRIPT
-- Order Status Chatbot - Complete Database Setup
-- =============================================================================

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2),
    shipping_address TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_bot_message BOOLEAN NOT NULL DEFAULT FALSE,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- =============================================================================
-- MIGRATION SECTION - Handle existing databases gracefully
-- =============================================================================

-- Add enabled column to customers table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'customers' AND column_name = 'enabled') THEN
        ALTER TABLE customers ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
        RAISE NOTICE 'Added enabled column to customers table';
    ELSE
        RAISE NOTICE 'Enabled column already exists in customers table';
    END IF;
END $$;

-- Add address column to customers table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'customers' AND column_name = 'address') THEN
        ALTER TABLE customers ADD COLUMN address TEXT;
        RAISE NOTICE 'Added address column to customers table';
    ELSE
        RAISE NOTICE 'Address column already exists in customers table';
    END IF;
END $$;

-- Add updated_date column to customers table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'customers' AND column_name = 'updated_date') THEN
        ALTER TABLE customers ADD COLUMN updated_date TIMESTAMP;
        RAISE NOTICE 'Added updated_date column to customers table';
    ELSE
        RAISE NOTICE 'Updated_date column already exists in customers table';
    END IF;
END $$;

-- Update existing customers to have enabled = true if not set
UPDATE customers SET enabled = TRUE WHERE enabled IS NULL;

-- Update existing customers to have updated_date = created_date if not set
UPDATE customers SET updated_date = created_date WHERE updated_date IS NULL;

-- =============================================================================
-- SAMPLE DATA INSERTION
-- =============================================================================

-- Insert sample customer data with CORRECT BCrypt encrypted passwords
-- Password for all customers is "password123"
-- Using the working hash that you manually verified
INSERT INTO customers (email, password, first_name, last_name, phone, address, created_date, enabled) VALUES
('john.doe@example.com', '$2a$10$g4N7IgmTZ8uZ2bnN37gXPegLh0khbGpzqmTVDLQr9L88nmes42mTi', 'John', 'Doe', '+1-555-123-4567', '123 Main St, New York, NY 10001', CURRENT_TIMESTAMP, TRUE),
('sarah.smith@example.com', '$2a$10$g4N7IgmTZ8uZ2bnN37gXPegLh0khbGpzqmTVDLQr9L88nmes42mTi', 'Sarah', 'Smith', '+1-555-234-5678', '456 Oak Ave, Los Angeles, CA 90210', CURRENT_TIMESTAMP, TRUE),
('mike.johnson@example.com', '$2a$10$g4N7IgmTZ8uZ2bnN37gXPegLh0khbGpzqmTVDLQr9L88nmes42mTi', 'Mike', 'Johnson', '+1-555-345-6789', '789 Pine St, Chicago, IL 60601', CURRENT_TIMESTAMP, TRUE),
('lisa.wilson@example.com', '$2a$10$g4N7IgmTZ8uZ2bnN37gXPegLh0khbGpzqmTVDLQr9L88nmes42mTi', 'Lisa', 'Wilson', '+1-555-456-7890', '321 Elm St, Miami, FL 33101', CURRENT_TIMESTAMP, TRUE),
('david.brown@example.com', '$2a$10$g4N7IgmTZ8uZ2bnN37gXPegLh0khbGpzqmTVDLQr9L88nmes42mTi', 'David', 'Brown', '+1-555-567-8901', '654 Maple Dr, Seattle, WA 98101', CURRENT_TIMESTAMP, TRUE)
ON CONFLICT (email) DO NOTHING;

-- Insert sample order data
INSERT INTO orders (order_number, customer_id, status, total_amount, shipping_address, created_date) VALUES
('ORD-001', 1, 'DELIVERED', 199.99, '123 Main St, New York, NY 10001', CURRENT_TIMESTAMP - INTERVAL '10 days'),
('ORD-002', 1, 'SHIPPED', 149.50, '123 Main St, New York, NY 10001', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('ORD-003', 1, 'PROCESSING', 89.99, '123 Main St, New York, NY 10001', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('ORD-004', 2, 'DELIVERED', 299.99, '456 Oak Ave, Los Angeles, CA 90210', CURRENT_TIMESTAMP - INTERVAL '15 days'),
('ORD-005', 2, 'SHIPPED', 179.99, '456 Oak Ave, Los Angeles, CA 90210', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('ORD-006', 3, 'PROCESSING', 129.99, '789 Pine St, Chicago, IL 60601', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('ORD-007', 4, 'DELIVERED', 399.99, '321 Elm St, Miami, FL 33101', CURRENT_TIMESTAMP - INTERVAL '20 days'),
('ORD-008', 5, 'SHIPPED', 259.99, '654 Maple Dr, Seattle, WA 98101', CURRENT_TIMESTAMP - INTERVAL '7 days')
ON CONFLICT (order_number) DO NOTHING;

-- =============================================================================
-- PERFORMANCE OPTIMIZATION
-- =============================================================================

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_conversations_customer_id ON conversations(customer_id);
CREATE INDEX IF NOT EXISTS idx_conversations_session_id ON conversations(session_id);

-- =============================================================================
-- PERMISSIONS AND SECURITY
-- =============================================================================

-- Grant permissions to the application user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO chatbot;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO chatbot;

-- =============================================================================
-- VERIFICATION AND LOGGING
-- =============================================================================

-- Log the initialization completion
DO $$
BEGIN
    RAISE NOTICE 'Database initialization completed successfully!';
    RAISE NOTICE 'Sample customers created with password: password123';
    RAISE NOTICE 'Sample orders created for testing';
    RAISE NOTICE 'All indexes and permissions configured';
END $$; 
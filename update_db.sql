-- Migration script for Admin Verification System & User Authentication
-- Adds verification_status to lost_items and found_items
-- Creates users table

USE lost_found_db;

-- Update lost_items table (Ignore error if column already exists)
-- ALTER TABLE lost_items ADD COLUMN verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' AFTER status;

-- Update found_items table (Ignore error if column already exists)
-- ALTER TABLE found_items ADD COLUMN verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' AFTER status;

-- Drop old admins table if it exists
DROP TABLE IF EXISTS admins;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    token_expiry TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Register initial admin (password: admin123 - for now)
INSERT INTO users (email, password, full_name, role, is_verified) 
VALUES ('vengalarishi143@gmail.com', 'admin123', 'System Administrator', 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE role='ADMIN', is_verified=TRUE;

-- Mark existing records as APPROVED so they stay visible
UPDATE lost_items SET verification_status = 'APPROVED';
UPDATE found_items SET verification_status = 'APPROVED';

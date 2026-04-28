-- ==========================================================
-- Lost And Found Management System - Database Schema
-- GOKARAJU RANGARAJU INSTITUTE OF ENGINEERING AND TECHNOLOGY
-- Department of Computer Science and Engineering
-- ==========================================================

CREATE DATABASE IF NOT EXISTS lost_found_db;
USE lost_found_db;

-- -----------------------------------------------------------
-- Table: users
-- Stores all registered users
-- -----------------------------------------------------------
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

-- -----------------------------------------------------------
-- Table: lost_items
-- Stores all items reported as lost
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS lost_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    date_lost DATE NOT NULL,
    location VARCHAR(200) NOT NULL,
    reporter_name VARCHAR(100) NOT NULL,
    contact VARCHAR(50) NOT NULL,
    status ENUM('Lost', 'Claimed') DEFAULT 'Lost',
    verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    claim_requested BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------
-- Table: found_items
-- Stores all items reported as found
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS found_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    date_found DATE NOT NULL,
    location VARCHAR(200) NOT NULL,
    finder_name VARCHAR(100) NOT NULL,
    contact VARCHAR(50) NOT NULL,
    status ENUM('Found', 'Returned') DEFAULT 'Found',
    verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    return_requested BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

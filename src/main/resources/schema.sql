CREATE DATABASE IF NOT EXISTS bms;

USE bms;

-- =======================
-- USERS TABLE
-- =======================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    role ENUM('CUSTOMER', 'EMPLOYEE', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

-- =======================
-- ACCOUNTS TABLE
-- =======================
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    number VARCHAR(20) UNIQUE NOT NULL,
    type ENUM('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT') NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
);

-- =======================
-- TRANSACTIONS TABLE
-- =======================
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_account_id BIGINT,
    receiver_account_id BIGINT,
    reference VARCHAR(36) UNIQUE NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    fee DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('SUCCESS', 'FAILED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_sender
        FOREIGN KEY (sender_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_receiver
        FOREIGN KEY (receiver_account_id) REFERENCES accounts(id)
);

-- =======================
-- LOANS TABLE
-- =======================
CREATE TABLE loans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    disbursement_account_id BIGINT NOT NULL,
    type ENUM('PERSONAL', 'HOME', 'CAR', 'BUSINESS') NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    remaining_amount DECIMAL(15, 2) NOT NULL,
    duration_months INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'DISBURSED', 'PAID')
        DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_loans_customer
        FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_loans_disbursement_account
        FOREIGN KEY (disbursement_account_id) REFERENCES accounts(id)
);

-- =======================
-- PAYMENTS TABLE
-- =======================
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_loan
        FOREIGN KEY (loan_id) REFERENCES loans(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_payments_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- =======================
-- CARDS TABLE
-- =======================
CREATE TABLE cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    number VARCHAR(16) UNIQUE NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_cards_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
            ON DELETE CASCADE
);

-- =======================
-- INDEXES
-- =======================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_accounts_customer ON accounts(customer_id);
CREATE INDEX idx_transactions_sender ON transactions(sender_account_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_account_id);
CREATE INDEX idx_loans_customer ON loans(customer_id);

CREATE DATABASE IF NOT EXISTS bms;

USE bms;

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ACCOUNTS TABLE
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_number BIGINT NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0.00),
    account_type ENUM('SAVINGS', 'CURRENT') NOT NULL,
    account_status ENUM('PENDING', 'ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'PENDING' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_accounts_user_id
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_accounts_user_id (user_id)
);

-- TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NULL,
    receiver_id BIGINT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER') NOT NULL,
    transaction_status ENUM('SUCCESS', 'FAILED') NOT NULL,
    transaction_reference CHAR(36) UNIQUE DEFAULT (UUID()) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_transactions_sender_id
        FOREIGN KEY (sender_id) REFERENCES accounts(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_transactions_receiver_id
        FOREIGN KEY (receiver_id) REFERENCES accounts(id)
            ON DELETE RESTRICT ON UPDATE CASCADE
);

-- LOANS TABLE
CREATE TABLE IF NOT EXISTS loans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    principal_amount DECIMAL(10, 2) NOT NULL CHECK (principal_amount > 0),
    installment_amount DECIMAL(10, 2) NOT NULL CHECK (installment_amount > 0),
    installment_count INT NOT NULL CHECK (installment_count > 0),
    remaining_installments INT NOT NULL,
    loan_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'PAID') DEFAULT 'PENDING' NOT NULL,
    next_due_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_loans_user_id
     FOREIGN KEY (user_id) REFERENCES users(id)
         ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_loans_account_id
        FOREIGN KEY (account_id) REFERENCES accounts(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_loans_user_id (user_id),
    INDEX idx_loans_account_id (account_id)
);

-- PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_status ENUM('SUCCESS', 'FAILED') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_payments_loan_id
        FOREIGN KEY (loan_id) REFERENCES loans(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payments_account_id
        FOREIGN KEY (account_id) REFERENCES accounts(id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_payments_loan_id (loan_id),
    INDEX idx_payments_account_id (account_id)
);

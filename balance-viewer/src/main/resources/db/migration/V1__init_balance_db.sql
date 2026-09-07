CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS user_balances (
    user_id UUID PRIMARY KEY,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT timezone('utc', now())
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY
);

-- Inbox-таблица для паттерна Outbox
CREATE TABLE tx_inbox (
    transaction_id UUID PRIMARY KEY
);
CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    external_id UUID NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,     -- DEPOSIT, WITHDRAWAL
    created_at TIMESTAMP NOT NULL DEFAULT timezone('utc', now())
);

-- Индекс для быстрой выборки всех транзакций конкретного пользователя
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);

CREATE TABLE IF NOT EXISTS user_balances (
    user_id UUID PRIMARY KEY,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT timezone('utc', now())
);

-- Таблица для паттерна Outbox
CREATE TABLE tx_outbox (
    transaction_id UUID  PRIMARY KEY,
    user_id UUID NOT NULL,
    payload JSONB NOT NULL,
    sent BOOLEAN NOT NULL
);
-- Индекс для быстрого поиска неотправленных событий
CREATE INDEX idx_tx_outbox_status ON tx_outbox(sent);
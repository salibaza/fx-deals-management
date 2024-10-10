CREATE DATABASE IF NOT EXISTS fxdeals;

USE fxdeals;

CREATE TABLE Deal (
    unique_id VARCHAR(255) NOT NULL PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    deal_timestamp TIMESTAMP NOT NULL,
    deal_amount DECIMAL(19, 4) NOT NULL
);
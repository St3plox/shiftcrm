CREATE TABLE seller
(
    id                BIGINT PRIMARY KEY,
    name              VARCHAR(255)                        NOT NULL,
    contact_info      VARCHAR(255)                        NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create Transaction Table
CREATE TABLE transactions
(
    id               BIGINT PRIMARY KEY,
    seller_id        BIGINT                              NOT NULL,
    amount           DECIMAL(10, 2)                      NOT NULL,
    payment_type     VARCHAR(20)                         NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_seller FOREIGN KEY (seller_id) REFERENCES seller (id) ON DELETE CASCADE
);
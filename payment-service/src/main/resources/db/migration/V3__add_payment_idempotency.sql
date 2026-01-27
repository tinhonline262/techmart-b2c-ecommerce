-- V3: Add payment idempotency table and update payment provider records

-- Create payment_idempotency table for callback deduplication
CREATE TABLE IF NOT EXISTS payment_idempotency (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    payment_id BIGINT NOT NULL,
    provider_id VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    request_hash VARCHAR(128),
    response_data TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    INDEX idx_idempotency_key (idempotency_key),
    INDEX idx_payment_id (payment_id),
    INDEX idx_created_at (created_at),
    CONSTRAINT fk_idempotency_payment FOREIGN KEY (payment_id) REFERENCES payment(id)
);

-- Ensure all payment providers exist with correct settings
INSERT INTO payment_provider (id, enabled, name, configure_url, landing_view_component_name, additional_settings, created_at, updated_at)
VALUES 
    ('VNPAY', true, 'VNPay', 'https://vnpay.vn', 'VNPayLanding', 
     '{"supportedMethods":["VNPAY","BANK_TRANSFER","ATM_CARD","CREDIT_CARD"]}', 
     NOW(), NOW()),
    ('MOMO', true, 'MoMo', 'https://momo.vn', 'MoMoLanding', 
     '{"supportedMethods":["MOMO","WALLET","ATM_CARD","CREDIT_CARD"]}', 
     NOW(), NOW()),
    ('PAYPAL', true, 'PayPal', 'https://paypal.com', 'PayPalLanding', 
     '{"supportedMethods":["PAYPAL","CREDIT_CARD"],"currencies":["USD","EUR"]}', 
     NOW(), NOW()),
    ('COD', true, 'Cash on Delivery', NULL, 'CODLanding', 
     '{"supportedMethods":["COD"],"maxAmount":10000000,"minAmount":10000}', 
     NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    additional_settings = VALUES(additional_settings),
    updated_at = NOW();

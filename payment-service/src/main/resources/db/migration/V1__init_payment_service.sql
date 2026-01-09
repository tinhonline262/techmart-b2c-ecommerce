CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          order_number VARCHAR(255) NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          currency VARCHAR(10) DEFAULT 'VND',
                          payment_method VARCHAR(50) NOT NULL,
                          payment_provider VARCHAR(50),
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                          provider_transaction_id VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          INDEX idx_order_id (order_id),
                          INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Flyway migration file:
-- - Location: payment-service/src/main/resources/db/migration
-- - Filename: V1__init_payment_service.sql
--
-- Content:

-- Create payment_provider table
CREATE TABLE payment_provider (
                                  id VARCHAR(50) PRIMARY KEY,
                                  enabled BOOLEAN NOT NULL DEFAULT true,
                                  name VARCHAR(255) NOT NULL,
                                  configure_url VARCHAR(500),
                                  landing_view_component_name VARCHAR(255),
                                  additional_settings TEXT,
                                  media_id BIGINT,
                                  version INT NOT NULL DEFAULT 0,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  created_by VARCHAR(100),
                                  updated_by VARCHAR(100),
                                  UNIQUE KEY uk_provider_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create payment table
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT,
                         checkout_id VARCHAR(50),
                         amount DECIMAL(19,2) NOT NULL,
                         payment_fee DECIMAL(19,2) DEFAULT 0,
                         payment_method VARCHAR(50) NOT NULL,
                         payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                         gateway_transaction_id VARCHAR(255),
                         failure_message VARCHAR(1000),
                         payment_provider_checkout_id VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         created_by VARCHAR(100),
                         updated_by VARCHAR(100),
                         INDEX idx_order_id (order_id),
                         INDEX idx_checkout_id (checkout_id),
                         INDEX idx_payment_status (payment_status),
                         INDEX idx_gateway_transaction_id (gateway_transaction_id),
                         INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
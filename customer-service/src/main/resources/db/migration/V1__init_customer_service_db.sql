CREATE TABLE user_address (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,

                              user_id VARCHAR(255) NOT NULL,
                              address_id BIGINT NOT NULL,
                              is_active BOOLEAN DEFAULT TRUE,

    -- audit fields
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Indexes
-- =========================
-- Lookup all addresses of a user quickly
CREATE INDEX idx_user_address_user_id ON user_address(user_id);

-- Lookup which users are associated with a given address
CREATE INDEX idx_user_address_address_id ON user_address(address_id);
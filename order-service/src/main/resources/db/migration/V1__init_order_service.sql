-- -- Create orders table
-- CREATE TABLE orders (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     order_number VARCHAR(255) NOT NULL UNIQUE,
--     customer_id BIGINT NOT NULL,
--     customer_name VARCHAR(255) NOT NULL,
--     customer_email VARCHAR(255) NOT NULL,
--     order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
--     total_amount DECIMAL(10, 2) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     INDEX idx_order_number (order_number),
--     INDEX idx_customer_id (customer_id),
--     INDEX idx_status (status),
--     INDEX idx_order_date (order_date)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
--
-- -- Create order_items table
-- CREATE TABLE order_items (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     order_id BIGINT NOT NULL,
--     product_id BIGINT NOT NULL,
--     product_name VARCHAR(255) NOT NULL,
--     product_code VARCHAR(100) NOT NULL,
--     quantity INT NOT NULL,
--     unit_price DECIMAL(10, 2) NOT NULL,
--     total_price DECIMAL(10, 2) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
--     INDEX idx_order_id (order_id),
--     INDEX idx_product_id (product_id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
--
-- -- Create order_status_history table for tracking order status changes
-- CREATE TABLE order_status_history (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     order_id BIGINT NOT NULL,
--     status VARCHAR(50) NOT NULL,
--     comments TEXT,
--     changed_by VARCHAR(255),
--     changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
--     INDEX idx_order_id (order_id),
--     INDEX idx_changed_at (changed_at)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
--

-- V1__init_order_service.sql

-- =========================
-- Table: order_address
-- =========================
CREATE TABLE IF NOT EXISTS order_address (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             contact_name VARCHAR(255),
    phone VARCHAR(50),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(20),
    district_id BIGINT,
    district_name VARCHAR(100),
    state_or_province_id BIGINT,
    state_or_province_name VARCHAR(100),
    country_id BIGINT,
    country_name VARCHAR(100)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Table: `order`
-- =========================
CREATE TABLE IF NOT EXISTS `order` (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       email VARCHAR(255),
    shipping_address_id BIGINT,
    billing_address_id BIGINT,
    note TEXT,
    total_tax FLOAT,
    total_discount_amount FLOAT,
    number_item INT,
    promotion_code VARCHAR(100),
    total_amount DECIMAL(19,2),
    total_shipment_fee DECIMAL(19,2),
    status VARCHAR(50),
    shipment_method_id VARCHAR(50),
    shipment_status VARCHAR(50),
    payment_status VARCHAR(50),
    payment_id BIGINT,
    checkout_id VARCHAR(50),
    reject_reason VARCHAR(255),
    payment_method_id VARCHAR(50),
    progress VARCHAR(50),
    customer_id VARCHAR(50),
    last_error JSON,
    attributes JSON,
    total_shipment_tax DECIMAL(19,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shipping_address_id) REFERENCES order_address(id),
    FOREIGN KEY (billing_address_id) REFERENCES order_address(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Table: order_item
-- =========================
CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          product_id BIGINT,
                                          order_id BIGINT,
                                          name VARCHAR(255),
    quantity INT,
    price DECIMAL(19,2),
    description TEXT,
    discount_amount DECIMAL(19,2),
    tax_amount DECIMAL(19,2),
    tax_percent DECIMAL(5,2),
    shipment_fee DECIMAL(19,2),
    status VARCHAR(50),
    shipment_tax DECIMAL(19,2),
    processing_state JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Table: checkout
-- =========================
CREATE TABLE IF NOT EXISTS checkout (
                                        id CHAR(36) PRIMARY KEY,
    email VARCHAR(255),
    note TEXT,
    promotion_code VARCHAR(100),
    status VARCHAR(50),
    progress VARCHAR(50),
    customer_id VARCHAR(50),
    shipment_method_id VARCHAR(50),
    payment_method_id VARCHAR(50),
    shipping_address_id BIGINT,
    last_error JSON,
    attributes JSON,
    total_amount DECIMAL(19,2) DEFAULT 0,
    total_shipment_fee DECIMAL(19,2) DEFAULT 0,
    total_shipment_tax DECIMAL(19,2) DEFAULT 0,
    total_tax DECIMAL(19,2),
    total_discount_amount DECIMAL(19,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Table: checkout_item
-- =========================
CREATE TABLE IF NOT EXISTS checkout_item (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             product_id BIGINT,
                                             name VARCHAR(255),
    description TEXT,
    quantity INT,
    price DECIMAL(19,2),
    tax DECIMAL(19,2),
    shipment_fee DECIMAL(19,2),
    shipment_tax DECIMAL(19,2),
    discount_amount DECIMAL(19,2),
    checkout_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (checkout_id) REFERENCES checkout(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

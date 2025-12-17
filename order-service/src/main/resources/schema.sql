-- Create orders table
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_number VARCHAR(255) NOT NULL UNIQUE,
                        customer_id BIGINT NOT NULL,
                        customer_name VARCHAR(255) NOT NULL,
                        customer_email VARCHAR(255) NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                        total_amount DECIMAL(10, 2) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        INDEX idx_order_number (order_number),
                        INDEX idx_customer_id (customer_id),
                        INDEX idx_status (status),
                        INDEX idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create order_items table
CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             product_sku VARCHAR(100) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10, 2) NOT NULL,
                             total_price DECIMAL(10, 2) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             INDEX idx_order_id (order_id),
                             INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create order_status_history table for tracking order status changes
CREATE TABLE order_status_history (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      order_id BIGINT NOT NULL,
                                      status VARCHAR(50) NOT NULL,
                                      comments TEXT,
                                      changed_by VARCHAR(255),
                                      changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                                      INDEX idx_order_id (order_id),
                                      INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample orders
INSERT INTO orders (order_number, customer_id, customer_name, customer_email, order_date, status, total_amount, created_at, updated_at)
VALUES
    ('ORD-2024-001', 1, 'John Doe', 'john.doe@example.com', NOW(), 'PENDING', 299.99, NOW(), NOW()),
    ('ORD-2024-002', 2, 'Jane Smith', 'jane.smith@example.com', NOW(), 'CONFIRMED', 549.98, NOW(), NOW()),
    ('ORD-2024-003', 1, 'John Doe', 'john.doe@example.com', NOW(), 'SHIPPED', 149.99, NOW(), NOW());

-- Insert sample order items for order 1
INSERT INTO order_items (order_id, product_id, product_name, product_sku, quantity, unit_price, total_price, created_at, updated_at)
VALUES
    (1, 101, 'Laptop Stand', 'LAP-STD-001', 2, 49.99, 99.98, NOW(), NOW()),
    (1, 102, 'Wireless Mouse', 'WRL-MSE-001', 1, 29.99, 29.99, NOW(), NOW()),
    (1, 103, 'USB-C Cable', 'USB-CBL-001', 3, 19.99, 59.97, NOW(), NOW());

-- Insert sample order items for order 2
INSERT INTO order_items (order_id, product_id, product_name, product_sku, quantity, unit_price, total_price, created_at, updated_at)
VALUES
    (2, 104, 'Mechanical Keyboard', 'MEC-KBD-001', 1, 149.99, 149.99, NOW(), NOW()),
    (2, 105, 'Monitor 27"', 'MON-27-001', 1, 399.99, 399.99, NOW(), NOW());

-- Insert sample order items for order 3
INSERT INTO order_items (order_id, product_id, product_name, product_sku, quantity, unit_price, total_price, created_at, updated_at)
VALUES
    (3, 106, 'Webcam HD', 'WBC-HD-001', 1, 149.99, 149.99, NOW(), NOW());

-- Insert order status history
INSERT INTO order_status_history (order_id, status, comments, changed_by, changed_at)
VALUES
    (1, 'PENDING', 'Order created', 'SYSTEM', NOW()),
    (2, 'PENDING', 'Order created', 'SYSTEM', NOW()),
    (2, 'CONFIRMED', 'Payment confirmed', 'SYSTEM', NOW()),
    (3, 'PENDING', 'Order created', 'SYSTEM', NOW()),
    (3, 'CONFIRMED', 'Payment confirmed', 'SYSTEM', NOW()),
    (3, 'SHIPPED', 'Order shipped via FedEx', 'ADMIN', NOW());




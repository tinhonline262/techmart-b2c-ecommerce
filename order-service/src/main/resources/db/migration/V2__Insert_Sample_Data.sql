-- Insert sample orders
INSERT INTO orders (order_number, customer_id, customer_name, customer_email, order_date, status, total_amount, created_at, updated_at)
VALUES
    ('ORD-2024-001', 1, 'John Doe', 'john.doe@example.com', NOW(), 'PENDING', 299.99, NOW(), NOW()),
    ('ORD-2024-002', 2, 'Jane Smith', 'jane.smith@example.com', NOW(), 'CONFIRMED', 549.98, NOW(), NOW()),
    ('ORD-2024-003', 1, 'John Doe', 'john.doe@example.com', NOW(), 'SHIPPED', 149.99, NOW(), NOW());

-- Insert sample order items for order 1
INSERT INTO order_items (order_id, product_id, product_name, product_code, quantity, unit_price, total_price, created_at, updated_at)
VALUES
    (1, 101, 'Laptop Stand', 'LAP-STD-001', 2, 49.99, 99.98, NOW(), NOW()),
    (1, 102, 'Wireless Mouse', 'WRL-MSE-001', 1, 29.99, 29.99, NOW(), NOW()),
    (1, 103, 'USB-C Cable', 'USB-CBL-001', 3, 19.99, 59.97, NOW(), NOW());

-- Insert sample order items for order 2
INSERT INTO order_items (order_id, product_id, product_name, product_code, quantity, unit_price, total_price, created_at, updated_at)
VALUES
    (2, 104, 'Mechanical Keyboard', 'MEC-KBD-001', 1, 149.99, 149.99, NOW(), NOW()),
    (2, 105, 'Monitor 27"', 'MON-27-001', 1, 399.99, 399.99, NOW(), NOW());

-- Insert sample order items for order 3
INSERT INTO order_items (order_id, product_id, product_name, product_code, quantity, unit_price, total_price, created_at, updated_at)
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


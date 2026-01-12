-- =========================
-- V3__simulate_order_data.sql
-- =========================

-- Create temporary numbers table for generating sequences
CREATE TEMPORARY TABLE numbers (
    n INT PRIMARY KEY
);

INSERT INTO numbers (n)
SELECT a.n + b.n * 10 + 1
FROM
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
WHERE a.n + b.n * 10 < 100; -- generate 1..100

-- =========================
-- Insert OrderAddresses
-- =========================
INSERT INTO order_address (
    contact_name, phone, address_line1, address_line2, city,
    zip_code, district_id, district_name, state_or_province_id, state_or_province_name,
    country_id, country_name
)
SELECT
    CONCAT('Customer ', n),
    CONCAT('0900', LPAD(n, 4, '0')),
    CONCAT('Street ', n),
    CONCAT('Block ', n),
    CONCAT('City ', n),
    LPAD(n, 5, '0'),
    n,
    CONCAT('District ', n),
    n,
    CONCAT('State ', n),
    1,
    'Vietnam'
FROM numbers
WHERE n <= 20;

-- =========================
-- Insert Orders
-- =========================
INSERT INTO `order` (
    email, shipping_address_id, billing_address_id, note,
    total_tax, total_discount_amount, number_item,
    promotion_code, total_amount, total_shipment_fee,
    status, shipment_method_id, shipment_status,
    payment_status, payment_id, checkout_id, reject_reason
)
SELECT
    CONCAT('user', n, '@example.com'),
    FLOOR(RAND() * 20) + 1,
    FLOOR(RAND() * 20) + 1,
    CONCAT('Order note ', n),
    ROUND(RAND() * 50, 2),
    ROUND(RAND() * 30, 2),
    FLOOR(RAND() * 5) + 1,
    IF(RAND() > 0.5, CONCAT('PROMO', n), NULL),
    ROUND(RAND() * 500 + 50, 2),
    ROUND(RAND() * 20, 2),
    'PENDING',
    IF(RAND() > 0.5, 'HOME_DELIVERY', 'STORE_PICKUP'),
    'PROCESSING',
    'UNPAID',
    n,
    CONCAT('checkout-', n),
    NULL
FROM numbers
WHERE n <= 50;

-- =========================
-- Insert OrderItems
-- =========================
INSERT INTO order_item (
    product_id, order_id, name, quantity, price, description,
    discount_amount, tax_amount, tax_percent, shipment_fee, status
)
SELECT
    FLOOR(RAND() * 50) + 1,
    o.id,
    CONCAT('Product ', FLOOR(RAND() * 50) + 1),
    FLOOR(RAND() * 5) + 1,
    ROUND(RAND() * 500 + 50, 2),
    CONCAT('Item note ', n),
    ROUND(RAND() * 20, 2),
    ROUND(RAND() * 10, 2),
    ROUND(RAND() * 10, 2),
    ROUND(RAND() * 10, 2),
    'PENDING'
FROM numbers
         JOIN `order` o ON o.id = n
WHERE n <= 50;

-- =========================
-- Insert Checkout
-- =========================
INSERT INTO checkout (
    id, email, note, promotion_code, status, payment_method_id,
    total_amount, total_shipment_fee, total_discount_amount, total_tax
)
SELECT
    UUID(),
    CONCAT('user', n, '@example.com'),
    CONCAT('Checkout note ', n),
    IF(RAND() > 0.5, CONCAT('PROMO', n), NULL),
    'PENDING',
    IF(RAND() > 0.5, 'PAYPAL', 'CREDIT_CARD'),
    ROUND(RAND() * 500 + 50, 2),
    ROUND(RAND() * 20, 2),
    ROUND(RAND() * 30, 2),
    ROUND(RAND() * 50, 2)
FROM numbers
WHERE n <= 30;

-- =========================
-- Insert CheckoutItems
-- =========================
INSERT INTO checkout_item (
    product_id, name, quantity, price, tax, discount_amount, checkout_id
)
SELECT
    FLOOR(RAND() * 50) + 1,
    CONCAT('Product ', FLOOR(RAND() * 50) + 1),
    FLOOR(RAND() * 5) + 1,
    ROUND(RAND() * 500 + 50, 2),
    ROUND(RAND() * 10, 2),
    ROUND(RAND() * 20, 2),
    c.id
FROM numbers
         JOIN checkout c ON c.id IS NOT NULL
WHERE n <= 30;

-- Clean up
DROP TEMPORARY TABLE numbers;

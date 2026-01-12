-- =========================
-- Simulate data for cart_item (Flyway-safe)
-- =========================
-- Assumption:
-- - 10 customers: customer-1 .. customer-10
-- - 50 products: product_id 1 .. 50
-- - Each customer has 10 products in cart
-- - Composite PK (customer_id, product_id) is guaranteed unique

INSERT IGNORE INTO cart_item (
    customer_id,
    product_id,
    quantity,
    created_at,
    updated_at
)
SELECT
    CONCAT('customer-', c.n) AS customer_id,
    p.n AS product_id,
    FLOOR(RAND() * 5) + 1 AS quantity,
    NOW(),
    NOW()
FROM
    (
        SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
        UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    ) c
        JOIN
    (
        SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
        UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    ) p;

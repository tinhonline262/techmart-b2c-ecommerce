-- =========================
-- Seed data for user_address
-- =========================

INSERT IGNORE INTO user_address (
    user_id,
    address_id,
    is_active,
    created_at,
    updated_at
)
SELECT
    u.user_id,
    a.address_id,
    IF(RAND() > 0.5, TRUE, FALSE) AS is_active,
    NOW(),
    NOW()
FROM
    (
        SELECT 'user-1' AS user_id UNION ALL
        SELECT 'user-2' UNION ALL
        SELECT 'user-3' UNION ALL
        SELECT 'user-4' UNION ALL
        SELECT 'user-5' UNION ALL
        SELECT 'user-6' UNION ALL
        SELECT 'user-7' UNION ALL
        SELECT 'user-8' UNION ALL
        SELECT 'user-9' UNION ALL
        SELECT 'user-10'
    ) u
        JOIN
    (
        SELECT 1 AS address_id UNION ALL
        SELECT 2 UNION ALL
        SELECT 3 UNION ALL
        SELECT 4 UNION ALL
        SELECT 5 UNION ALL
        SELECT 6 UNION ALL
        SELECT 7 UNION ALL
        SELECT 8 UNION ALL
        SELECT 9 UNION ALL
        SELECT 10
    ) a
WHERE (RAND() < 0.3);  -- mỗi user có ~3 addresses random

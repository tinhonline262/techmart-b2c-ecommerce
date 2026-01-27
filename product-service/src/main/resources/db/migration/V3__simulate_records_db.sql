CREATE TEMPORARY TABLE numbers (
    n INT PRIMARY KEY
);

INSERT INTO numbers (n)
SELECT a.n + b.n * 10 + c.n * 100 + 1
FROM
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b,
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c
WHERE a.n + b.n * 10 + c.n * 100 < 1000;

INSERT INTO category (name, slug, description, display_order, is_published)
SELECT
    CONCAT('Category ', n),
    CONCAT('category-', n),
    CONCAT('Description ', n),
    n,
    TRUE
FROM numbers
WHERE n <= 10;


INSERT INTO brand (name, slug, is_published)
SELECT
    CONCAT('Brand ', n),
    CONCAT('brand-', n),
    TRUE
FROM numbers
WHERE n <= 20;


INSERT INTO product (
    name, slug, short_description, description, specification,
    sku, gtin, price,
    has_options, is_allowed_to_order, is_published, is_featured,
    is_visible_individually,
    stock_tracking_enabled, stock_quantity,
    meta_title, meta_keyword, meta_description,
    weight, dimension_unit, length, width, height,
    brand_id
)
SELECT
    CONCAT('Product ', n),
    CONCAT('product-', n),
    CONCAT('Short desc ', n),
    CONCAT('Description ', n),
    CONCAT('Specs ', n),
    CONCAT('SKU-', LPAD(n, 6, '0')),
    CONCAT('GTIN-', LPAD(n, 8, '0')),
    ROUND(RAND() * 10000000 + 5000000, 2),
    RAND() > 0.5,
    TRUE,
    TRUE,
    RAND() > 0.8,
    TRUE,
    TRUE,
    FLOOR(RAND() * 500),
    CONCAT('Meta title ', n),
    'electronics',
    CONCAT('Meta desc ', n),
    ROUND(RAND() * 5 + 0.5, 2),
    'cm',
    ROUND(RAND() * 50 + 10, 2),
    ROUND(RAND() * 50 + 10, 2),
    ROUND(RAND() * 50 + 10, 2),
    FLOOR(RAND() * 20) + 1
FROM numbers;


INSERT INTO product_category (product_id, category_id)
SELECT
    p.id,
    FLOOR(RAND() * 10) + 1
FROM product p;

INSERT INTO product_image (product_id, image_id, display_order, is_primary)
SELECT
    p.id,
    p.id * 10 + img.n,
    img.n,
    img.n = 1
FROM product p
         JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3
) img;


INSERT INTO product_related (product_id, related_product_id)
SELECT
    p.id,
    ((p.id + r.n) % (SELECT COUNT(*) FROM product)) + 1
FROM product p
    JOIN (
    SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3
    ) r
WHERE ((p.id + r.n) % (SELECT COUNT(*) FROM product)) + 1 <> p.id;

INSERT INTO product_option (product_id, name)
SELECT id, 'Size' FROM product;

INSERT INTO product_option (product_id, name)
SELECT id, 'Color' FROM product;


INSERT INTO product_option_value (product_option_id, value, display_type, display_order)
SELECT
    po.id,
    s.value,
    'text',
    s.display_order
FROM product_option po
         JOIN (
    SELECT 'S' value, 1 display_order
    UNION ALL SELECT 'M', 2
    UNION ALL SELECT 'L', 3
    UNION ALL SELECT 'XL', 4
) s
WHERE po.name = 'Size';


INSERT INTO product_option_value (product_option_id, value, display_type, display_order)
SELECT
    po.id,
    c.value,
    'color',
    c.display_order
FROM product_option po
         JOIN (
    SELECT 'Red' value, 1 display_order
    UNION ALL SELECT 'Blue', 2
    UNION ALL SELECT 'Black', 3
    UNION ALL SELECT 'White', 4
) c
WHERE po.name = 'Color';


INSERT INTO product_option_combination (product_id, value, display_order)
SELECT
    p.id,
    'Size=M;Color=Black',
    1
FROM product p;


DROP TEMPORARY TABLE numbers;

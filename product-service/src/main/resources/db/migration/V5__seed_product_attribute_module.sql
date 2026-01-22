INSERT INTO product_attribute_group (name) VALUES
                                               ('Technical Specs'),
                                               ('Display'),
                                               ('Storage'),
                                               ('Physical'),
                                               ('General');


INSERT INTO product_attribute (name, product_attribute_group_id) VALUES
-- Technical Specs
('CPU', 1),
('RAM', 1),
('GPU', 1),

-- Display
('Screen Size', 2),
('Resolution', 2),

-- Storage
('Storage', 3),

-- Physical
('Weight', 4),
('Color', 4),

-- General
('Material', 5),
('Brand', 5);

INSERT INTO product_template (name) VALUES
                                        ('Laptop'),
                                        ('Smartphone'),
                                        ('Clothing');


-- Laptop template
INSERT INTO product_attribute_template (product_template_id, product_attribute_id, display_order)
SELECT 1, id, ROW_NUMBER() OVER () FROM product_attribute
WHERE name IN ('CPU', 'RAM', 'GPU', 'Screen Size', 'Resolution', 'Storage', 'Weight', 'Brand');

-- Smartphone template
INSERT INTO product_attribute_template (product_template_id, product_attribute_id, display_order)
SELECT 2, id, ROW_NUMBER() OVER () FROM product_attribute
WHERE name IN ('CPU', 'RAM', 'Screen Size', 'Resolution', 'Storage', 'Weight', 'Brand');

-- Clothing template
INSERT INTO product_attribute_template (product_template_id, product_attribute_id, display_order)
SELECT 3, id, ROW_NUMBER() OVER () FROM product_attribute
WHERE name IN ('Color', 'Material', 'Brand');


-- Laptop (product_id = 1)
INSERT INTO product_attribute_value (product_id, product_attribute_id, value)
SELECT 1, id,
       CASE name
           WHEN 'CPU' THEN 'Intel Core i7-13700H'
           WHEN 'RAM' THEN '32GB'
           WHEN 'GPU' THEN 'NVIDIA RTX 4060'
           WHEN 'Screen Size' THEN '16 inch'
           WHEN 'Resolution' THEN '2560x1600'
           WHEN 'Storage' THEN '1TB SSD'
           WHEN 'Weight' THEN '2.1kg'
           WHEN 'Brand' THEN 'Dell'
           END
FROM product_attribute
WHERE name IN ('CPU','RAM','GPU','Screen Size','Resolution','Storage','Weight','Brand');


-- Smartphone (product_id = 2)
INSERT INTO product_attribute_value (product_id, product_attribute_id, value)
SELECT 2, id,
       CASE name
           WHEN 'CPU' THEN 'Snapdragon 8 Gen 2'
           WHEN 'RAM' THEN '12GB'
           WHEN 'Screen Size' THEN '6.7 inch'
           WHEN 'Resolution' THEN '3200x1440'
           WHEN 'Storage' THEN '256GB'
           WHEN 'Weight' THEN '195g'
           WHEN 'Brand' THEN 'Samsung'
           END
FROM product_attribute
WHERE name IN ('CPU','RAM','Screen Size','Resolution','Storage','Weight','Brand');

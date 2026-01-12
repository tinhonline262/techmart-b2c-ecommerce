-- =========================
-- Seed Vietnam location data
-- =========================

-- 1️⃣ Country
INSERT IGNORE INTO country (
    id, name, code2, code3, is_billing_enabled, is_shipping_enabled, is_city_enabled, is_zip_code_enabled, is_district_enabled, created_at, updated_at
) VALUES
(1, 'Vietnam', 'VN', 'VNM', TRUE, TRUE, TRUE, TRUE, TRUE, NOW(), NOW());

-- 2️⃣ States / Provinces (Tỉnh / Thành phố)
-- Lấy 5 tỉnh demo
INSERT IGNORE INTO state_or_province (
    id, code, name, type, country_id, created_at, updated_at
) VALUES
(1, 'HN', 'Hanoi', 'City', 1, NOW(), NOW()),
(2, 'HCM', 'Ho Chi Minh', 'City', 1, NOW(), NOW()),
(3, 'DN', 'Da Nang', 'City', 1, NOW(), NOW()),
(4, 'HP', 'Hai Phong', 'City', 1, NOW(), NOW()),
(5, 'CT', 'Can Tho', 'City', 1, NOW(), NOW());

-- 3️⃣ Districts (Quận / Huyện)
-- 3 quận demo cho mỗi tỉnh
INSERT IGNORE INTO district (
    id, name, type, location, state_or_province_id, created_at, updated_at
) VALUES
-- Hanoi
(1, 'Ba Dinh', 'District', NULL, 1, NOW(), NOW()),
(2, 'Hoan Kiem', 'District', NULL, 1, NOW(), NOW()),
(3, 'Tay Ho', 'District', NULL, 1, NOW(), NOW()),
-- Ho Chi Minh
(4, 'District 1', 'District', NULL, 2, NOW(), NOW()),
(5, 'District 3', 'District', NULL, 2, NOW(), NOW()),
(6, 'Binh Thanh', 'District', NULL, 2, NOW(), NOW()),
-- Da Nang
(7, 'Hai Chau', 'District', NULL, 3, NOW(), NOW()),
(8, 'Thanh Khe', 'District', NULL, 3, NOW(), NOW()),
(9, 'Son Tra', 'District', NULL, 3, NOW(), NOW()),
-- Hai Phong
(10, 'Ngo Quyen', 'District', NULL, 4, NOW(), NOW()),
(11, 'Le Chan', 'District', NULL, 4, NOW(), NOW()),
(12, 'Hai An', 'District', NULL, 4, NOW(), NOW()),
-- Can Tho
(13, 'Ninh Kieu', 'District', NULL, 5, NOW(), NOW()),
(14, 'Binh Thuy', 'District', NULL, 5, NOW(), NOW()),
(15, 'Cai Rang', 'District', NULL, 5, NOW(), NOW());

-- 4️⃣ Addresses (demo 10 addresses)
INSERT IGNORE INTO address (
    id, contact_name, phone, address_line_1, address_line_2, city, zip_code, district_id, state_or_province_id, country_id, created_at, updated_at
) VALUES
(1, 'Nguyen Van A', '0901234567', '123 Ly Thuong Kiet', NULL, 'Hanoi', '100000', 1, 1, 1, NOW(), NOW()),
(2, 'Tran Thi B', '0912345678', '456 Hoang Quoc Viet', NULL, 'Hanoi', '100000', 2, 1, 1, NOW(), NOW()),
(3, 'Le Van C', '0923456789', '789 Tay Ho', NULL, 'Hanoi', '100000', 3, 1, 1, NOW(), NOW()),
(4, 'Pham Thi D', '0934567890', '12 District 1', NULL, 'Ho Chi Minh', '700000', 4, 2, 1, NOW(), NOW()),
(5, 'Hoang Van E', '0945678901', '34 District 3', NULL, 'Ho Chi Minh', '700000', 5, 2, 1, NOW(), NOW()),
(6, 'Bui Thi F', '0956789012', '56 Binh Thanh', NULL, 'Ho Chi Minh', '700000', 6, 2, 1, NOW(), NOW()),
(7, 'Nguyen Van G', '0967890123', '78 Hai Chau', NULL, 'Da Nang', '550000', 7, 3, 1, NOW(), NOW()),
(8, 'Tran Thi H', '0978901234', '90 Thanh Khe', NULL, 'Da Nang', '550000', 8, 3, 1, NOW(), NOW()),
(9, 'Le Van I', '0989012345', '12 Son Tra', NULL, 'Da Nang', '550000', 9, 3, 1, NOW(), NOW()),
(10, 'Pham Thi J', '0990123456', '34 Ninh Kieu', NULL, 'Can Tho', '900000', 13, 5, 1, NOW(), NOW());

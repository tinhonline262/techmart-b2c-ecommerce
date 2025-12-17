-- Product Service Database Schema

-- Drop tables if they exist
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

-- Categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Products table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sku VARCHAR(100) UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT DEFAULT 0,
    category_id BIGINT,
    brand VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_product_name (name),
    INDEX idx_product_sku (sku),
    INDEX idx_product_category (category_id),
    INDEX idx_product_brand (brand),
    INDEX idx_product_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product images table
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_product_image_product (product_id),
    INDEX idx_product_image_primary (is_primary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert demo categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Men and women clothing'),
('Books', 'Books, magazines, and educational materials'),
('Home & Kitchen', 'Home appliances and kitchen essentials'),
('Sports & Outdoors', 'Sports equipment and outdoor gear'),
('Toys & Games', 'Toys, games, and entertainment'),
('Beauty & Personal Care', 'Beauty products and personal care items'),
('Automotive', 'Car parts and automotive accessories');

-- Insert demo products (75 products)
INSERT INTO products (name, description, sku, price, quantity_in_stock, category_id, brand, is_active) VALUES
-- Electronics (1-25)
('iPhone 15 Pro', 'Latest Apple smartphone with A17 Pro chip', 'ELEC-001', 999.99, 50, 1, 'Apple', TRUE),
('MacBook Pro M3', '14-inch laptop with M3 chip', 'ELEC-002', 1999.99, 30, 1, 'Apple', TRUE),
('Samsung Galaxy S24', 'Flagship Android phone', 'ELEC-003', 899.99, 45, 1, 'Samsung', TRUE),
('Sony WH-1000XM5', 'Noise-cancelling headphones', 'ELEC-004', 399.99, 100, 1, 'Sony', TRUE),
('iPad Air', '10.9-inch tablet', 'ELEC-005', 599.99, 60, 1, 'Apple', TRUE),
('Dell XPS 15', 'Premium Windows laptop', 'ELEC-006', 1499.99, 25, 1, 'Dell', TRUE),
('AirPods Pro', 'Wireless earbuds with ANC', 'ELEC-007', 249.99, 120, 1, 'Apple', TRUE),
('Nintendo Switch', 'Hybrid gaming console', 'ELEC-008', 299.99, 80, 1, 'Nintendo', TRUE),
('LG OLED TV 55"', '4K OLED Smart TV', 'ELEC-009', 1299.99, 20, 1, 'LG', TRUE),
('Bose SoundLink', 'Portable Bluetooth speaker', 'ELEC-010', 129.99, 90, 1, 'Bose', TRUE),
('Google Pixel 8', 'Android smartphone', 'ELEC-011', 699.99, 55, 1, 'Google', TRUE),
('Kindle Paperwhite', 'E-reader', 'ELEC-012', 139.99, 150, 1, 'Amazon', TRUE),
('Xbox Series X', 'Gaming console', 'ELEC-013', 499.99, 40, 1, 'Microsoft', TRUE),
('PlayStation 5', 'Gaming console', 'ELEC-014', 499.99, 35, 1, 'Sony', TRUE),
('Canon EOS R6', 'Mirrorless camera', 'ELEC-015', 2499.99, 15, 1, 'Canon', TRUE),
('Apple Watch SE', 'Smartwatch', 'ELEC-016', 249.99, 90, 1, 'Apple', TRUE),
('Samsung Galaxy Tab', 'Android tablet', 'ELEC-017', 449.99, 50, 1, 'Samsung', TRUE),
('Logitech MX Master', 'Wireless mouse', 'ELEC-018', 99.99, 150, 1, 'Logitech', TRUE),
('Anker PowerBank', '20000mAh battery', 'ELEC-019', 49.99, 200, 1, 'Anker', TRUE),

-- Clothing (26-40)
('Nike Air Max', 'Running shoes', 'CLOT-001', 129.99, 200, 2, 'Nike', TRUE),
('Levi''s 501 Jeans', 'Classic fit jeans', 'CLOT-002', 69.99, 150, 2, 'Levi''s', TRUE),
('Adidas Hoodie', 'Comfortable cotton hoodie', 'CLOT-003', 59.99, 180, 2, 'Adidas', TRUE),
('Columbia Jacket', 'Waterproof winter jacket', 'CLOT-004', 149.99, 90, 2, 'Columbia', TRUE),
('Under Armour T-Shirt', 'Sports t-shirt', 'CLOT-005', 29.99, 250, 2, 'Under Armour', TRUE),
('North Face Fleece', 'Warm fleece jacket', 'CLOT-006', 99.99, 120, 2, 'North Face', TRUE),
('Ray-Ban Aviators', 'Classic sunglasses', 'CLOT-007', 179.99, 200, 2, 'Ray-Ban', TRUE),
('Converse All Stars', 'Classic canvas sneakers', 'CLOT-008', 59.99, 300, 2, 'Converse', TRUE),
('Champion Sweatpants', 'Comfortable joggers', 'CLOT-009', 44.99, 180, 2, 'Champion', TRUE),
('Carhartt Beanie', 'Warm winter hat', 'CLOT-010', 24.99, 250, 2, 'Carhartt', TRUE),
('Patagonia Vest', 'Down vest', 'CLOT-011', 179.99, 80, 2, 'Patagonia', TRUE),
('New Balance 990', 'Running shoes', 'CLOT-012', 184.99, 100, 2, 'New Balance', TRUE),
('Herschel Backpack', 'Classic backpack', 'CLOT-013', 69.99, 150, 2, 'Herschel', TRUE),
('Timberland Boots', 'Waterproof boots', 'CLOT-014', 198.99, 70, 2, 'Timberland', TRUE),
('Calvin Klein Jeans', 'Slim fit jeans', 'CLOT-015', 89.99, 120, 2, 'Calvin Klein', TRUE),
('Brooks Running Shoes', 'Ghost 15', 'CLOT-016', 139.99, 100, 2, 'Brooks', TRUE),
('Allbirds Wool Runners', 'Sustainable sneakers', 'CLOT-017', 98.99, 120, 2, 'Allbirds', TRUE),

-- Home & Kitchen (41-55)
('KitchenAid Mixer', 'Stand mixer for baking', 'HOME-001', 349.99, 40, 4, 'KitchenAid', TRUE),
('Dyson V15 Vacuum', 'Cordless vacuum cleaner', 'HOME-002', 649.99, 35, 4, 'Dyson', TRUE),
('Instant Pot', '6-quart pressure cooker', 'HOME-003', 99.99, 120, 4, 'Instant Pot', TRUE),
('Ninja Blender', 'Professional blender', 'HOME-004', 129.99, 80, 4, 'Ninja', TRUE),
('Keurig Coffee Maker', 'Single-serve coffee maker', 'HOME-005', 149.99, 100, 4, 'Keurig', TRUE),
('Vitamix Blender', 'Professional-grade blender', 'HOME-006', 449.99, 40, 4, 'Vitamix', TRUE),
('Le Creuset Dutch Oven', 'Enameled cast iron', 'HOME-007', 379.99, 35, 4, 'Le Creuset', TRUE),
('Roomba i7', 'Robot vacuum cleaner', 'HOME-008', 599.99, 45, 4, 'iRobot', TRUE),
('Nespresso Machine', 'Espresso maker', 'HOME-009', 199.99, 70, 4, 'Nespresso', TRUE),
('Air Fryer XL', 'Large capacity air fryer', 'HOME-010', 129.99, 100, 4, 'Ninja', TRUE),
('Breville Toaster', '4-slice smart toaster', 'HOME-011', 179.99, 60, 4, 'Breville', TRUE),
('Cuisinart Food Processor', '14-cup processor', 'HOME-012', 249.99, 50, 4, 'Cuisinart', TRUE),
('Philips Air Purifier', 'HEPA air purifier', 'HOME-013', 299.99, 45, 4, 'Philips', TRUE),
('Nest Thermostat', 'Smart thermostat', 'HOME-014', 129.99, 80, 4, 'Google', TRUE),
('Ring Doorbell', 'Video doorbell', 'HOME-015', 99.99, 100, 4, 'Ring', TRUE),
('Staub Cocotte', 'Cast iron pot', 'HOME-016', 329.99, 30, 4, 'Staub', TRUE),
('Chemex Coffeemaker', 'Pour over coffee', 'HOME-017', 49.99, 100, 4, 'Chemex', TRUE),

-- Books (56-70)
('The Clean Code', 'Software engineering best practices', 'BOOK-001', 49.99, 500, 3, 'Pearson', TRUE),
('System Design Interview', 'Guide to system design', 'BOOK-002', 39.99, 400, 3, 'O''Reilly', TRUE),
('Atomic Habits', 'Self-improvement book', 'BOOK-003', 27.99, 600, 3, 'Penguin', TRUE),
('The Psychology of Money', 'Financial wisdom', 'BOOK-004', 24.99, 450, 3, 'Harriman House', TRUE),
('Designing Data-Intensive Applications', 'Modern data systems', 'BOOK-005', 54.99, 300, 3, 'O''Reilly', TRUE),
('The Pragmatic Programmer', 'Software development classic', 'BOOK-006', 54.99, 350, 3, 'Addison-Wesley', TRUE),
('Deep Work', 'Focus and productivity', 'BOOK-007', 29.99, 400, 3, 'Grand Central', TRUE),
('Sapiens', 'History of humankind', 'BOOK-008', 24.99, 500, 3, 'Harper', TRUE),
('Think and Grow Rich', 'Success principles', 'BOOK-009', 19.99, 600, 3, 'TarcherPerigee', TRUE),
('The Lean Startup', 'Entrepreneurship guide', 'BOOK-010', 34.99, 450, 3, 'Currency', TRUE),
('Zero to One', 'Startup insights', 'BOOK-011', 27.99, 380, 3, 'Currency', TRUE),
('Thinking Fast and Slow', 'Decision making', 'BOOK-012', 18.99, 420, 3, 'FSG', TRUE),
('The 4-Hour Workweek', 'Lifestyle design', 'BOOK-013', 21.99, 350, 3, 'Harmony', TRUE),
('Educated', 'Memoir', 'BOOK-014', 17.99, 480, 3, 'Random House', TRUE),
('Becoming', 'Michelle Obama memoir', 'BOOK-015', 32.99, 400, 3, 'Crown', TRUE),
('Good to Great', 'Business classic', 'BOOK-016', 29.99, 350, 3, 'HarperBusiness', TRUE),
('Start with Why', 'Leadership book', 'BOOK-017', 17.99, 400, 3, 'Portfolio', TRUE),

-- Sports & Outdoors (71-75)
('Yeti Cooler', '45-quart cooler', 'SPORT-001', 349.99, 50, 5, 'Yeti', TRUE),
('Coleman Tent', '6-person camping tent', 'SPORT-002', 199.99, 70, 5, 'Coleman', TRUE),
('Patagonia Backpack', 'Hiking backpack 40L', 'SPORT-003', 149.99, 85, 5, 'Patagonia', TRUE),
('GoPro HERO12', 'Action camera', 'SPORT-004', 449.99, 60, 5, 'GoPro', TRUE),
('Fitbit Charge 6', 'Fitness tracker', 'SPORT-005', 159.99, 150, 5, 'Fitbit', TRUE),
('Hydroflask 32oz', 'Insulated water bottle', 'SPORT-006', 44.99, 200, 5, 'Hydroflask', TRUE),
('Garmin Watch', 'GPS running watch', 'SPORT-007', 349.99, 60, 5, 'Garmin', TRUE),
('Osprey Daypack', '20L hiking daypack', 'SPORT-008', 89.99, 100, 5, 'Osprey', TRUE),
('Black Diamond Headlamp', 'LED headlamp', 'SPORT-009', 39.99, 150, 5, 'Black Diamond', TRUE),
('Manduka Yoga Mat', 'Premium yoga mat', 'SPORT-010', 79.99, 120, 5, 'Manduka', TRUE),
('REI Tent', '2-person backpacking tent', 'SPORT-011', 299.99, 40, 5, 'REI', TRUE),
('Thule Bike Rack', 'Hitch mount bike rack', 'SPORT-012', 449.99, 30, 5, 'Thule', TRUE),
('Pelican Cooler', '30-quart cooler', 'SPORT-013', 279.99, 50, 5, 'Pelican', TRUE),
('Traeger Grill', 'Pellet smoker grill', 'SPORT-014', 799.99, 25, 5, 'Traeger', TRUE),
('Yeti Tumbler', '30oz insulated tumbler', 'SPORT-015', 34.99, 300, 5, 'Yeti', TRUE),
('Jetboil Stove', 'Backpacking stove', 'SPORT-016', 109.99, 70, 5, 'Jetboil', TRUE),
('ENO Hammock', 'Camping hammock', 'SPORT-017', 69.99, 150, 5, 'ENO', TRUE),
('Theragun Mini', 'Massage gun', 'SPORT-018', 199.99, 80, 5, 'Therabody', TRUE),
('Bowflex Dumbbells', 'Adjustable dumbbells', 'SPORT-019', 549.99, 40, 5, 'Bowflex', TRUE),
('Peloton Mat', 'Exercise mat', 'SPORT-020', 59.99, 200, 5, 'Peloton', TRUE);

-- Insert demo product images (one primary image per product)
INSERT INTO product_images (product_id, image_url, is_primary, display_order) VALUES
-- Electronics (1-19)
(1, 'https://example.com/iphone15.jpg', TRUE, 1),
(2, 'https://example.com/macbook.jpg', TRUE, 1),
(3, 'https://example.com/galaxy.jpg', TRUE, 1),
(4, 'https://example.com/sony-headphones.jpg', TRUE, 1),
(5, 'https://example.com/ipad.jpg', TRUE, 1),
(6, 'https://example.com/xps15.jpg', TRUE, 1),
(7, 'https://example.com/airpods.jpg', TRUE, 1),
(8, 'https://example.com/switch.jpg', TRUE, 1),
(9, 'https://example.com/lgtv.jpg', TRUE, 1),
(10, 'https://example.com/bose.jpg', TRUE, 1),
(11, 'https://example.com/pixel8.jpg', TRUE, 1),
(12, 'https://example.com/kindle.jpg', TRUE, 1),
(13, 'https://example.com/xbox.jpg', TRUE, 1),
(14, 'https://example.com/ps5.jpg', TRUE, 1),
(15, 'https://example.com/canon.jpg', TRUE, 1),
(16, 'https://example.com/watchse.jpg', TRUE, 1),
(17, 'https://example.com/tab.jpg', TRUE, 1),
(18, 'https://example.com/mxmaster.jpg', TRUE, 1),
(19, 'https://example.com/anker.jpg', TRUE, 1),

-- Clothing (20-36)
(20, 'https://example.com/nike-shoes.jpg', TRUE, 1),
(21, 'https://example.com/levis.jpg', TRUE, 1),
(22, 'https://example.com/hoodie.jpg', TRUE, 1),
(23, 'https://example.com/jacket.jpg', TRUE, 1),
(24, 'https://example.com/tshirt.jpg', TRUE, 1),
(25, 'https://example.com/fleece.jpg', TRUE, 1),
(26, 'https://example.com/rayban.jpg', TRUE, 1),
(27, 'https://example.com/converse.jpg', TRUE, 1),
(28, 'https://example.com/joggers.jpg', TRUE, 1),
(29, 'https://example.com/beanie.jpg', TRUE, 1),
(30, 'https://example.com/vest.jpg', TRUE, 1),
(31, 'https://example.com/nb990.jpg', TRUE, 1),
(32, 'https://example.com/herschel.jpg', TRUE, 1),
(33, 'https://example.com/timberland.jpg', TRUE, 1),
(34, 'https://example.com/ckjeans.jpg', TRUE, 1),
(35, 'https://example.com/brooks.jpg', TRUE, 1),
(36, 'https://example.com/allbirds.jpg', TRUE, 1),

-- Home & Kitchen (37-53)
(37, 'https://example.com/mixer.jpg', TRUE, 1),
(38, 'https://example.com/vacuum.jpg', TRUE, 1),
(39, 'https://example.com/instantpot.jpg', TRUE, 1),
(40, 'https://example.com/blender.jpg', TRUE, 1),
(41, 'https://example.com/keurig.jpg', TRUE, 1),
(42, 'https://example.com/vitamix.jpg', TRUE, 1),
(43, 'https://example.com/lecreuset.jpg', TRUE, 1),
(44, 'https://example.com/roomba.jpg', TRUE, 1),
(45, 'https://example.com/nespresso.jpg', TRUE, 1),
(46, 'https://example.com/airfryer.jpg', TRUE, 1),
(47, 'https://example.com/toaster.jpg', TRUE, 1),
(48, 'https://example.com/foodproc.jpg', TRUE, 1),
(49, 'https://example.com/purifier.jpg', TRUE, 1),
(50, 'https://example.com/nest.jpg', TRUE, 1),
(51, 'https://example.com/ring.jpg', TRUE, 1),
(52, 'https://example.com/staub.jpg', TRUE, 1),
(53, 'https://example.com/chemex.jpg', TRUE, 1),

-- Books (54-70)
(54, 'https://example.com/cleancode.jpg', TRUE, 1),
(55, 'https://example.com/systemdesign.jpg', TRUE, 1),
(56, 'https://example.com/atomichabits.jpg', TRUE, 1),
(57, 'https://example.com/psychology-money.jpg', TRUE, 1),
(58, 'https://example.com/ddia.jpg', TRUE, 1),
(59, 'https://example.com/pragmatic.jpg', TRUE, 1),
(60, 'https://example.com/deepwork.jpg', TRUE, 1),
(61, 'https://example.com/sapiens.jpg', TRUE, 1),
(62, 'https://example.com/thinkrich.jpg', TRUE, 1),
(63, 'https://example.com/leanstartup.jpg', TRUE, 1),
(64, 'https://example.com/zerotoone.jpg', TRUE, 1),
(65, 'https://example.com/thinking.jpg', TRUE, 1),
(66, 'https://example.com/4hour.jpg', TRUE, 1),
(67, 'https://example.com/educated.jpg', TRUE, 1),
(68, 'https://example.com/becoming.jpg', TRUE, 1),
(69, 'https://example.com/goodtogreat.jpg', TRUE, 1),
(70, 'https://example.com/startwithwhy.jpg', TRUE, 1),

-- Sports & Outdoors (71-75)
(71, 'https://example.com/yeti.jpg', TRUE, 1),
(72, 'https://example.com/tent.jpg', TRUE, 1),
(73, 'https://example.com/backpack.jpg', TRUE, 1),
(74, 'https://example.com/gopro.jpg', TRUE, 1),
(75, 'https://example.com/fitbit.jpg', TRUE, 1),
(76, 'https://example.com/hydroflask.jpg', TRUE, 1),
(77, 'https://example.com/garmin.jpg', TRUE, 1),
(78, 'https://example.com/osprey.jpg', TRUE, 1),
(79, 'https://example.com/headlamp.jpg', TRUE, 1),
(80, 'https://example.com/yogamat.jpg', TRUE, 1),
(81, 'https://example.com/reitent.jpg', TRUE, 1),
(82, 'https://example.com/bikerack.jpg', TRUE, 1),
(83, 'https://example.com/pelican.jpg', TRUE, 1),
(84, 'https://example.com/traeger.jpg', TRUE, 1),
(85, 'https://example.com/tumbler.jpg', TRUE, 1),
(86, 'https://example.com/jetboil.jpg', TRUE, 1),
(87, 'https://example.com/eno.jpg', TRUE, 1),
(88, 'https://example.com/theragun.jpg', TRUE, 1),
(89, 'https://example.com/bowflex.jpg', TRUE, 1),
(90, 'https://example.com/pelotonmat.jpg', TRUE, 1);

-- Insert demo products
INSERT INTO products (name, description, sku, price, quantity_in_stock, category_id, brand, is_active) VALUES
('Laptop Pro 15', 'High-performance laptop with 15-inch display, 16GB RAM, 512GB SSD', 'ELEC-LAP-001', 1299.99, 50, 1, 'TechBrand', TRUE),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 'ELEC-MOU-001', 29.99, 200, 1, 'TechBrand', TRUE),
('Smartphone X12', 'Latest smartphone with 6.5-inch OLED display, 128GB storage', 'ELEC-PHO-001', 799.99, 100, 1, 'PhoneCo', TRUE),
('Bluetooth Headphones', 'Noise-canceling wireless headphones with 30-hour battery life', 'ELEC-HDP-001', 149.99, 75, 1, 'AudioMax', TRUE),
('4K Monitor 27"', 'Ultra HD 4K monitor with HDR support', 'ELEC-MON-001', 449.99, 30, 1, 'DisplayPro', TRUE),

('Men''s Cotton T-Shirt', 'Comfortable 100% cotton t-shirt', 'CLOT-TSH-001', 19.99, 500, 2, 'FashionCo', TRUE),
('Women''s Denim Jeans', 'Classic fit denim jeans', 'CLOT-JEA-001', 59.99, 300, 2, 'DenimStyle', TRUE),
('Running Shoes', 'Lightweight running shoes with cushioned sole', 'CLOT-SHO-001', 89.99, 150, 2, 'SportFit', TRUE),
('Winter Jacket', 'Warm winter jacket with water-resistant fabric', 'CLOT-JAC-001', 129.99, 80, 2, 'OutdoorWear', TRUE),
('Casual Sneakers', 'Comfortable casual sneakers for everyday wear', 'CLOT-SNK-001', 69.99, 200, 2, 'StreetStyle', TRUE),

('The Great Novel', 'Bestselling fiction novel', 'BOOK-FIC-001', 14.99, 1000, 3, 'PublishHouse', TRUE),
('Programming Guide', 'Complete guide to modern programming', 'BOOK-TEC-001', 49.99, 250, 3, 'TechBooks', TRUE),
('Cooking Mastery', 'Professional cooking techniques and recipes', 'BOOK-COO-001', 29.99, 400, 3, 'CulinaryPress', TRUE),

('Garden Tool Set', 'Complete 10-piece garden tool set', 'HOME-TOO-001', 79.99, 100, 4, 'GardenPro', TRUE),
('LED Desk Lamp', 'Adjustable LED desk lamp with touch control', 'HOME-LAM-001', 39.99, 180, 4, 'LightingCo', TRUE),
('Storage Organizer', 'Multi-compartment storage organizer', 'HOME-STO-001', 34.99, 220, 4, 'OrganizeIt', TRUE),

('Yoga Mat', 'Non-slip yoga mat with carrying strap', 'SPOR-YOG-001', 24.99, 350, 5, 'FitnessPro', TRUE),
('Camping Tent 4-Person', 'Waterproof camping tent for 4 people', 'SPOR-TEN-001', 159.99, 45, 5, 'OutdoorGear', TRUE),
('Mountain Bike', 'Durable mountain bike with 21-speed gears', 'SPOR-BIK-001', 599.99, 25, 5, 'BikeMax', TRUE),
('Water Bottle 1L', 'Insulated stainless steel water bottle', 'SPOR-BOT-001', 19.99, 500, 5, 'HydratePro', TRUE);

-- Insert demo product images
INSERT INTO product_images (product_id, image_url, is_primary, display_order) VALUES
(1, 'https://example.com/images/laptop-pro-15-front.jpg', TRUE, 1),
(1, 'https://example.com/images/laptop-pro-15-side.jpg', FALSE, 2),
(2, 'https://example.com/images/wireless-mouse.jpg', TRUE, 1),
(3, 'https://example.com/images/smartphone-x12.jpg', TRUE, 1),
(4, 'https://example.com/images/headphones.jpg', TRUE, 1),
(5, 'https://example.com/images/4k-monitor.jpg', TRUE, 1);


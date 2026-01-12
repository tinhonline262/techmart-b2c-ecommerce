-- Search & SEO
CREATE INDEX idx_product_name ON product(name);
CREATE UNIQUE INDEX idx_product_slug ON product(slug);

-- Business filters
CREATE INDEX idx_product_published ON product(is_published);
CREATE INDEX idx_product_featured ON product(is_featured);
CREATE INDEX idx_product_allowed_to_order ON product(is_allowed_to_order);
CREATE INDEX idx_product_visible ON product(is_visible_individually);

-- Inventory
CREATE INDEX idx_product_stock ON product(stock_quantity);

-- Brand
CREATE INDEX idx_product_brand ON product(brand_id);

-- SKU
CREATE UNIQUE INDEX idx_product_sku ON product(sku);

-- Category
CREATE UNIQUE INDEX idx_category_slug ON category(slug);
CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_parent ON category(parent_id);
CREATE INDEX idx_category_published ON category(is_published);

-- Brand
CREATE UNIQUE INDEX idx_brand_slug ON brand(slug);
CREATE INDEX idx_brand_name ON brand(name);
CREATE INDEX idx_brand_published ON brand(is_published);

-- Product-Category Relationship
CREATE INDEX idx_pc_product ON product_category(product_id);
CREATE INDEX idx_pc_category ON product_category(category_id);

-- Product Images
CREATE INDEX idx_product_image_product ON product_image(product_id);
CREATE INDEX idx_product_image_primary ON product_image(product_id, is_primary);

-- Related Products
CREATE INDEX idx_related_product ON product_related(product_id);
CREATE INDEX idx_related_related ON product_related(related_product_id);

-- Product Options
CREATE INDEX idx_product_option_product ON product_option(product_id);

-- Option Values
CREATE INDEX idx_option_value_option ON product_option_value(product_option_id);
CREATE INDEX idx_option_value_order ON product_option_value(display_order);

-- Option Combinations
CREATE INDEX idx_option_combination_product ON product_option_combination(product_id);

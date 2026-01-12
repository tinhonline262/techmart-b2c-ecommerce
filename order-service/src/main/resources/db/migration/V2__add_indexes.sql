-- V2__add_indexes.sql

-- =========================
-- Table: order_address
-- =========================
-- Index on city, district, state/province, country for search/filter
CREATE INDEX idx_order_address_city ON order_address(city);
CREATE INDEX idx_order_address_district_id ON order_address(district_id);
CREATE INDEX idx_order_address_state_id ON order_address(state_or_province_id);
CREATE INDEX idx_order_address_country_id ON order_address(country_id);

-- =========================
-- Table: `order`
-- =========================
-- Index on foreign keys
CREATE INDEX idx_order_shipping_address_id ON `order`(shipping_address_id);
CREATE INDEX idx_order_billing_address_id ON `order`(billing_address_id);

-- Index on commonly searched fields
CREATE INDEX idx_order_email ON `order`(email);
CREATE INDEX idx_order_checkout_id ON `order`(checkout_id);
CREATE INDEX idx_order_status ON `order`(status);
CREATE INDEX idx_order_payment_status ON `order`(payment_status);
CREATE INDEX idx_order_shipment_status ON `order`(shipment_status);
CREATE INDEX idx_order_shipment_method_id ON `order`(shipment_method_id);

-- =========================
-- Table: order_item
-- =========================
-- Index on foreign key
CREATE INDEX idx_order_item_order_id ON order_item(order_id);

-- Index on frequently queried product
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

-- =========================
-- Table: checkout
-- =========================
-- Index on commonly queried fields
CREATE INDEX idx_checkout_email ON checkout(email);
CREATE INDEX idx_checkout_status ON checkout(status);
CREATE INDEX idx_checkout_customer_id ON checkout(customer_id);

-- =========================
-- Table: checkout_item
-- =========================
-- Index on foreign key
CREATE INDEX idx_checkout_item_checkout_id ON checkout_item(checkout_id);

-- Index on frequently queried product
CREATE INDEX idx_checkout_item_product_id ON checkout_item(product_id);

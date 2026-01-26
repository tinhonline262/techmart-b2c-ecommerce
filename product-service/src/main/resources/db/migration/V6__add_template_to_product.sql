-- V6__add_template_to_product.sql
-- Add template_id column to product table and create foreign key relationship

ALTER TABLE product ADD COLUMN template_id BIGINT;

ALTER TABLE product ADD CONSTRAINT fk_product_template
FOREIGN KEY (template_id) REFERENCES product_template(id) ON DELETE SET NULL;

CREATE INDEX idx_product_template_id ON product(template_id);

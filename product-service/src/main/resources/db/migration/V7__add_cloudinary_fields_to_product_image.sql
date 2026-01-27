-- V7__add_cloudinary_fields_to_product_image.sql
-- Add Cloudinary integration fields to product_image table

ALTER TABLE product_image
    ADD COLUMN image_url VARCHAR(1000) NULL COMMENT 'Cloudinary secure URL for the image',
    ADD COLUMN cloudinary_public_id VARCHAR(500) NULL COMMENT 'Cloudinary public ID for the uploaded file',
    ADD COLUMN alt_text VARCHAR(255) NULL COMMENT 'Alternative text for the image',
    ADD COLUMN created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Image creation timestamp',
    ADD COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Image last update timestamp',
                                                                                                                 MODIFY COLUMN image_id BIGINT NULL COMMENT 'Legacy image ID - can be null for Cloudinary uploads',
                                                                                                                 MODIFY COLUMN is_primary TINYINT(1) DEFAULT 0 COMMENT 'Whether this is the primary product image';
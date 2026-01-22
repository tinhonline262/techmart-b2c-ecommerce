-- ========================================
-- PRODUCT ATTRIBUTE MODULE
-- ========================================
-- Purpose:
-- Flexible attribute system for products
-- Supports templates (Laptop, Phone, Clothing, etc.)
-- Allows dynamic attributes without schema changes
-- ========================================

-- ========================================
-- PRODUCT ATTRIBUTE GROUP
-- ========================================
CREATE TABLE product_attribute_group (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,

                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         created_by VARCHAR(255),
                                         last_modified_by VARCHAR(255),

                                         INDEX idx_attr_group_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ========================================
-- PRODUCT ATTRIBUTE
-- ========================================
CREATE TABLE product_attribute (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL,
                                   product_attribute_group_id BIGINT,

                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   created_by VARCHAR(255),
                                   last_modified_by VARCHAR(255),

                                   CONSTRAINT fk_attr_group
                                       FOREIGN KEY (product_attribute_group_id)
                                           REFERENCES product_attribute_group(id)
                                           ON DELETE SET NULL,

                                   INDEX idx_attr_name (name),
                                   INDEX idx_attr_group_id (product_attribute_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ========================================
-- PRODUCT TEMPLATE
-- ========================================
CREATE TABLE product_template (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  created_by VARCHAR(255),
                                  last_modified_by VARCHAR(255),

                                  INDEX idx_template_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ========================================
-- PRODUCT ATTRIBUTE TEMPLATE
-- ========================================
CREATE TABLE product_attribute_template (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            product_template_id BIGINT NOT NULL,
                                            product_attribute_id BIGINT NOT NULL,
                                            display_order INT DEFAULT 0,

                                            CONSTRAINT fk_attr_template_template
                                                FOREIGN KEY (product_template_id)
                                                    REFERENCES product_template(id)
                                                    ON DELETE CASCADE,

                                            CONSTRAINT fk_attr_template_attribute
                                                FOREIGN KEY (product_attribute_id)
                                                    REFERENCES product_attribute(id)
                                                    ON DELETE CASCADE,

                                            UNIQUE KEY uq_template_attribute (product_template_id, product_attribute_id),
                                            INDEX idx_template_id (product_template_id),
                                            INDEX idx_attribute_id (product_attribute_id),
                                            INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ========================================
-- PRODUCT ATTRIBUTE VALUE
-- ========================================
CREATE TABLE product_attribute_value (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         product_id BIGINT NOT NULL,
                                         product_attribute_id BIGINT NOT NULL,
                                         value TEXT,

                                         CONSTRAINT fk_attr_value_product
                                             FOREIGN KEY (product_id)
                                                 REFERENCES product(id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_attr_value_attribute
                                             FOREIGN KEY (product_attribute_id)
                                                 REFERENCES product_attribute(id)
                                                 ON DELETE CASCADE,

                                         UNIQUE KEY uq_product_attribute (product_id, product_attribute_id),
                                         INDEX idx_product_id (product_id),
                                         INDEX idx_attribute_id (product_attribute_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

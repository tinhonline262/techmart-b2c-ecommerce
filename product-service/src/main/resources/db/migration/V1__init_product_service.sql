-- -- Product Service Database Schema
--
-- -- Categories table
-- CREATE TABLE categories (
--                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                             name VARCHAR(100) NOT NULL,
--                             description TEXT,
--                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                             INDEX idx_category_name (name)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
--
-- -- Products table
-- CREATE TABLE products (
--                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                           name VARCHAR(255) NOT NULL,
--                           description TEXT,
--                           sku VARCHAR(100) UNIQUE,
--                           price DECIMAL(10, 2) NOT NULL,
--                           category_id BIGINT,
--                           brand VARCHAR(100),
--                           is_active BOOLEAN DEFAULT TRUE,
--                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                           FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
--                           INDEX idx_product_name (name),
--                           INDEX idx_product_sku (sku),
--                           INDEX idx_product_category (category_id),
--                           INDEX idx_product_brand (brand),
--                           INDEX idx_product_active (is_active)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
--
-- -- Product images table
-- CREATE TABLE product_images (
--                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                                 product_id BIGINT,
--                                 image_url VARCHAR(500) NOT NULL,
--                                 is_primary BOOLEAN DEFAULT FALSE,
--                                 display_order INT DEFAULT 0,
--                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                                 FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
--                                 INDEX idx_product_image_product (product_id),
--                                 INDEX idx_product_image_primary (is_primary)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         slug VARCHAR(255) UNIQUE,
                         short_description TEXT,
                         description TEXT,
                         specification TEXT,

                         sku VARCHAR(100),
                         gtin VARCHAR(100),

                         price DECIMAL(15,2) NOT NULL,

                         has_options BOOLEAN DEFAULT FALSE,
                         is_allowed_to_order BOOLEAN DEFAULT TRUE,
                         is_published BOOLEAN DEFAULT FALSE,
                         is_featured BOOLEAN DEFAULT FALSE,
                         is_visible_individually BOOLEAN DEFAULT TRUE,

                         stock_tracking_enabled BOOLEAN DEFAULT TRUE,
                         stock_quantity BIGINT DEFAULT 0,

                         tax_class_id BIGINT,

                         meta_title VARCHAR(255),
                         meta_keyword VARCHAR(255),
                         meta_description TEXT,

                         thumbnail_media_id BIGINT,

                         weight DECIMAL(10,2),
                         dimension_unit VARCHAR(50),
                         length DECIMAL(10,2),
                         width DECIMAL(10,2),
                         height DECIMAL(10,2),

                         brand_id BIGINT,

                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          slug VARCHAR(255) UNIQUE,
                          description TEXT,
                          display_order INT DEFAULT 0,

                          meta_keyword VARCHAR(255),
                          meta_description TEXT,

                          is_published BOOLEAN DEFAULT FALSE,
                          image_id BIGINT,

                          parent_id BIGINT,
                          CONSTRAINT fk_category_parent
                              FOREIGN KEY (parent_id) REFERENCES category(id)
) ENGINE=InnoDB;


CREATE TABLE brand (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       slug VARCHAR(255) UNIQUE,
                       is_published BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE product_category (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  product_id BIGINT NOT NULL,
                                  category_id BIGINT NOT NULL,

                                  CONSTRAINT fk_pc_product
                                      FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_pc_category
                                      FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,

                                  UNIQUE KEY uq_product_category (product_id, category_id)
) ENGINE=InnoDB;

CREATE TABLE product_image (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               product_id BIGINT NOT NULL,
                               image_id BIGINT NOT NULL,
                               display_order INT DEFAULT 0,
                               is_primary BOOLEAN DEFAULT FALSE,

                               CONSTRAINT fk_product_image_product
                                   FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE product_related (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 related_product_id BIGINT NOT NULL,

                                 CONSTRAINT fk_related_product
                                     FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_related_related
                                     FOREIGN KEY (related_product_id) REFERENCES product(id) ON DELETE CASCADE,

                                 UNIQUE KEY uq_product_related (product_id, related_product_id)
) ENGINE=InnoDB;

CREATE TABLE product_option (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id BIGINT NOT NULL,
                                name VARCHAR(255) NOT NULL,

                                CONSTRAINT fk_product_option_product
                                    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE product_option_value (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      product_option_id BIGINT NOT NULL,
                                      value VARCHAR(255) NOT NULL,
                                      display_type VARCHAR(50),
                                      display_order INT DEFAULT 0,

                                      CONSTRAINT fk_option_value_option
                                          FOREIGN KEY (product_option_id) REFERENCES product_option(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE TABLE product_option_combination (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            product_id BIGINT NOT NULL,
                                            value VARCHAR(255),
                                            display_order INT DEFAULT 0,

                                            CONSTRAINT fk_option_combination_product
                                                FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB;


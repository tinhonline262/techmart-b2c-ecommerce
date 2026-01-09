-- Create warehouse table first (because other tables reference it)
CREATE TABLE warehouse (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(450) NOT NULL,
                           address_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           UNIQUE KEY uk_warehouse_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create inventories table with final structure (including warehouse_id, correct column names)
CREATE TABLE inventories (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             product_id BIGINT NOT NULL,
                             sku VARCHAR(100) NOT NULL,
                             warehouse_id BIGINT NOT NULL,
                             quantity BIGINT NOT NULL DEFAULT 0,
                             reserved_quantity BIGINT NOT NULL DEFAULT 0,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             UNIQUE KEY uk_inventory_product (product_id),
                             INDEX idx_inventory_sku (sku),
                             INDEX idx_inventory_warehouse (warehouse_id),
                             CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create inventory_transactions table with final structure (StockHistory pattern)
CREATE TABLE inventory_transactions (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        product_id BIGINT NOT NULL,
                                        adjusted_quantity BIGINT NOT NULL,
                                        note VARCHAR(450),
                                        warehouse_id BIGINT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        INDEX idx_inventory_product (product_id),
                                        INDEX idx_transaction_warehouse (warehouse_id),
                                        CONSTRAINT fk_transaction_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create reserved_orders table to track inventory reservations during order process
CREATE TABLE reserved_orders (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 order_id VARCHAR(100) NOT NULL,
                                 product_id BIGINT NOT NULL,
                                 sku VARCHAR(100) NOT NULL,
                                 warehouse_id BIGINT NOT NULL,
                                 reserved_quantity BIGINT NOT NULL,
                                 status VARCHAR(50) NOT NULL DEFAULT 'RESERVED',
                                 expires_at TIMESTAMP NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 INDEX idx_reserved_order_id (order_id),
                                 INDEX idx_reserved_product (product_id),
                                 INDEX idx_reserved_sku (sku),
                                 INDEX idx_reserved_warehouse (warehouse_id),
                                 INDEX idx_reserved_status (status),
                                 INDEX idx_reserved_expires (expires_at),
                                 CONSTRAINT fk_reserved_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add comment for status field values
-- Status values: RESERVED (initial), CONFIRMED (order completed), CANCELLED (order cancelled/failed), EXPIRED (reservation timed out)

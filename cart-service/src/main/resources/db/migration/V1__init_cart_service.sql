

CREATE TABLE IF NOT EXISTS cart_item (
                                         customer_id VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,

    -- audit fields (from AbstractAuditEntity)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- composite primary key
    PRIMARY KEY (customer_id, product_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_cart_item_customer_id ON cart_item(customer_id);
CREATE INDEX idx_cart_item_product_id ON cart_item(product_id);
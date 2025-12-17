UPDATE order_items
SET product_sku = product_code
WHERE product_sku IS NULL;

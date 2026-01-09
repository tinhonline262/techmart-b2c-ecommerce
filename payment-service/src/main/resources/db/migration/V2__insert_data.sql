INSERT INTO payments (
    order_id,
    order_number,
    amount,
    currency,
    payment_method,
    payment_provider,
    status,
    provider_transaction_id,
    created_at,
    updated_at
) VALUES
-- Successful payments
(1, 'ORD-2025-00001', 999.99, 'VND', 'CREDIT_CARD', 'VNPAY', 'COMPLETED', 'vnp_TxnRef_1735523401', '2025-12-15 10:30:00', '2025-12-15 10:32:15'),
(2, 'ORD-2025-00002', 2599.98, 'VND', 'BANK_TRANSFER', 'MOMO', 'COMPLETED', 'momo_20251216_12345678', '2025-12-16 14:20:30', '2025-12-16 14:23:10'),
(3, 'ORD-2025-00003', 129.99, 'VND', 'EWALLET', 'ZALOPAY', 'COMPLETED', 'zp_20251217_abc123xyz', '2025-12-17 09:15:45', '2025-12-17 09:17:20'),
(4, 'ORD-2025-00004', 449.99, 'VND', 'CREDIT_CARD', 'VNPAY', 'COMPLETED', 'vnp_TxnRef_1735608902', '2025-12-18 11:45:00', '2025-12-18 11:47:30'),
(5, 'ORD-2025-00005', 1999.99, 'VND', 'BANK_TRANSFER', 'MOMO', 'COMPLETED', 'momo_20251219_98765432', '2025-12-19 16:10:00', '2025-12-19 16:13:45'),

-- Pending payments (customer still completing payment)
(6, 'ORD-2025-00006', 699.99, 'VND', 'EWALLET', 'ZALOPAY', 'PENDING', 'zp_20251229_pending123', '2025-12-29 20:30:00', '2025-12-29 20:30:00'),
(7, 'ORD-2025-00007', 89.99, 'VND', 'CREDIT_CARD', 'VNPAY', 'PENDING', 'vnp_TxnRef_1735812345', '2025-12-30 08:15:00', '2025-12-30 08:15:00'),

-- Failed payments
(8, 'ORD-2025-00008', 1499.99, 'VND', 'CREDIT_CARD', 'VNPAY', 'FAILED', 'vnp_TxnRef_1735556789', '2025-12-20 13:40:00', '2025-12-20 13:42:10'),
(9, 'ORD-2025-00009', 299.99, 'VND', 'EWALLET', 'MOMO', 'FAILED', 'momo_20251221_failed001', '2025-12-21 17:25:30', '2025-12-21 17:27:00'),

-- Refunded payments
(10, 'ORD-2025-00010', 599.99, 'VND', 'CREDIT_CARD', 'VNPAY', 'REFUNDED', 'vnp_TxnRef_1735701234', '2025-12-22 10:00:00', '2025-12-28 14:30:00'),
(11, 'ORD-2025-00011', 1299.99, 'VND', 'BANK_TRANSFER', 'MOMO', 'PARTIALLY_REFUNDED', 'momo_20251223_55556666', '2025-12-23 12:15:00', '2025-12-29 11:45:00'),

-- Cash on Delivery (common in Vietnam)
(12, 'ORD-2025-00012', 349.99, 'VND', 'COD', NULL, 'PENDING', NULL, '2025-12-24 09:50:00', '2025-12-24 09:50:00'),
(13, 'ORD-2025-00013', 79.99, 'VND', 'COD', NULL, 'COMPLETED', NULL, '2025-12-26 15:20:00', '2025-12-27 10:30:00'),  -- Paid on delivery

-- International-style payment (USD example, though default is VND)
(14, 'ORD-2025-00014', 49.99, 'USD', 'CREDIT_CARD', 'STRIPE', 'COMPLETED', 'pi_3QAbCdEfGhIjKlMnOpQrStUv', '2025-12-25 22:00:00', '2025-12-25 22:02:30'),

-- Recent payment from today (December 30, 2025)
(15, 'ORD-2025-00015', 159.99, 'VND', 'EWALLET', 'ZALOPAY', 'COMPLETED', 'zp_20251230_success789', '2025-12-30 11:45:00', '2025-12-30 11:47:20');
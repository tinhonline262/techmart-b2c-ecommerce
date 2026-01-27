-- Insert warehouses
INSERT INTO warehouse (name, address_id) VALUES
                                             ('Main Warehouse', 1),
                                             ('Secondary Warehouse', 2);

-- Insert initial inventory data (with warehouse assignment)
INSERT INTO inventories (product_id, sku, warehouse_id, quantity, reserved_quantity) VALUES
-- Products 1-50 → Main Warehouse (id = 1)
(1, 'SKU-000001', 1, 48, 2), (2, 'SKU-000002', 1, 28, 2), (3, 'SKU-000003', 1, 42, 3),
(4, 'SKU-000004', 1, 95, 5), (5, 'SKU-000005', 1, 58, 2), (6, 'SKU-000006', 1, 23, 2),
(7, 'SKU-000007', 1, 115, 5), (8, 'SKU-000008', 1, 78, 2), (9, 'SKU-000009', 1, 18, 2),
(10, 'SKU-000010', 1, 88, 2), (11, 'SKU-000011', 1, 52, 3), (12, 'SKU-000012', 1, 145, 5),
(13, 'SKU-000013', 1, 38, 2), (14, 'SKU-000014', 1, 33, 2), (15, 'SKU-000015', 1, 14, 1),
(16, 'SKU-000016', 1, 87, 3), (17, 'SKU-000017', 1, 48, 2), (18, 'SKU-000018', 1, 148, 2),
(19, 'SKU-000019', 1, 195, 5), (20, 'SKU-000020', 1, 195, 5),
(21, 'SKU-000021', 1, 145, 5), (22, 'SKU-000022', 1, 175, 5), (23, 'SKU-000023', 1, 85, 5),
(24, 'SKU-000024', 1, 245, 5), (25, 'SKU-000025', 1, 115, 5), (26, 'SKU-000026', 1, 195, 5),
(27, 'SKU-000027', 1, 290, 10), (28, 'SKU-000028', 1, 175, 5), (29, 'SKU-000029', 1, 245, 5),
(30, 'SKU-000030', 1, 75, 5), (31, 'SKU-000031', 1, 95, 5), (32, 'SKU-000032', 1, 145, 5),
(33, 'SKU-000033', 1, 65, 5), (34, 'SKU-000034', 1, 115, 5), (35, 'SKU-000035', 1, 95, 5),
(36, 'SKU-000036', 1, 115, 5), (37, 'SKU-000037', 1, 38, 2), (38, 'SKU-000038', 1, 33, 2),
(39, 'SKU-000039', 1, 115, 5), (40, 'SKU-000040', 1, 75, 5), (41, 'SKU-000041', 1, 95, 5),
(42, 'SKU-000042', 1, 38, 2), (43, 'SKU-000043', 1, 33, 2), (44, 'SKU-000044', 1, 43, 2),
(45, 'SKU-000045', 1, 65, 5), (46, 'SKU-000046', 1, 95, 5), (47, 'SKU-000047', 1, 55, 5),
(48, 'SKU-000048', 1, 48, 2), (49, 'SKU-000049', 1, 43, 2), (50, 'SKU-000050', 1, 75, 5),

-- Products 51-90 → Secondary Warehouse (id = 2)
(51, 'SKU-000051', 2, 95, 5), (52, 'SKU-000052', 2, 28, 2), (53, 'SKU-000053', 2, 95, 5),
(54, 'SKU-000054', 2, 495, 5), (55, 'SKU-000055', 2, 395, 5), (56, 'SKU-000056', 2, 595, 5),
(57, 'SKU-000057', 2, 445, 5), (58, 'SKU-000058', 2, 295, 5), (59, 'SKU-000059', 2, 345, 5),
(60, 'SKU-000060', 2, 395, 5), (61, 'SKU-000061', 2, 495, 5), (62, 'SKU-000062', 2, 595, 5),
(63, 'SKU-000063', 2, 445, 5), (64, 'SKU-000064', 2, 375, 5), (65, 'SKU-000065', 2, 415, 5),
(66, 'SKU-000066', 2, 345, 5), (67, 'SKU-000067', 2, 475, 5), (68, 'SKU-000068', 2, 395, 5),
(69, 'SKU-000069', 2, 345, 5), (70, 'SKU-000070', 2, 395, 5), (71, 'SKU-000071', 2, 48, 2),
(72, 'SKU-000072', 2, 68, 2), (73, 'SKU-000073', 2, 83, 2), (74, 'SKU-000074', 2, 58, 2),
(75, 'SKU-000075', 2, 145, 5), (76, 'SKU-000076', 2, 195, 5), (77, 'SKU-000077', 2, 58, 2),
(78, 'SKU-000078', 2, 95, 5), (79, 'SKU-000079', 2, 145, 5), (80, 'SKU-000080', 2, 115, 5),
(81, 'SKU-000081', 2, 38, 2), (82, 'SKU-000082', 2, 28, 2), (83, 'SKU-000083', 2, 48, 2),
(84, 'SKU-000084', 2, 23, 2), (85, 'SKU-000085', 2, 295, 5), (86, 'SKU-000086', 2, 68, 2),
(87, 'SKU-000087', 2, 145, 5), (88, 'SKU-000088', 2, 78, 2), (89, 'SKU-000089', 2, 38, 2),
(90, 'SKU-000090', 2, 195, 5);


-- Insert initial transactions (converted to new format)
-- You can add note to describe the old type if needed
INSERT INTO inventory_transactions (product_id, adjusted_quantity, note, warehouse_id) VALUES
                                                                                           (1, 50, 'Initial stock-in (MANUAL)', 1),
                                                                                           (7, 120, 'Initial stock-in (MANUAL)', 1),
                                                                                           (20, 200, 'Initial stock-in (MANUAL)', 1),
                                                                                           (54, 500, 'Initial stock-in (MANUAL)', 2),
                                                                                           (71, 50, 'Initial stock-in (MANUAL)', 2),

                                                                                           (1, -2, 'Outbound sale (ORDER 1001)', 1),
                                                                                           (7, -5, 'Outbound sale (ORDER 1002)', 1),
                                                                                           (20, -5, 'Outbound sale (ORDER 1003)', 1),

-- Reservations and releases can be handled via business logic later
-- Here we just reflect the net effect already in inventories table
-- If you want to keep history, you can add reserve/release as notes too
                                                                                           (1, -2, 'Reservation (ORDER 1004)', 1),  -- reserved
                                                                                           (4, 5, 'Release reservation (ORDER 1005)', 1),  -- released back
                                                                                           (20, -5, 'Reservation (ORDER 1006)', 1);
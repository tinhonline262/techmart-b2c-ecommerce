-- Insert warehouses
INSERT INTO warehouse (name, address_id) VALUES
                                             ('Main Warehouse', 1),
                                             ('Secondary Warehouse', 2);

-- Insert initial inventory data (with warehouse assignment)
INSERT INTO inventories (product_id, sku, warehouse_id, quantity, reserved_quantity) VALUES
-- Products 1-50 → Main Warehouse (id = 1)
(1, 'ELEC-001', 1, 48, 2), (2, 'ELEC-002', 1, 28, 2), (3, 'ELEC-003', 1, 42, 3),
(4, 'ELEC-004', 1, 95, 5), (5, 'ELEC-005', 1, 58, 2), (6, 'ELEC-006', 1, 23, 2),
(7, 'ELEC-007', 1, 115, 5), (8, 'ELEC-008', 1, 78, 2), (9, 'ELEC-009', 1, 18, 2),
(10, 'ELEC-010', 1, 88, 2), (11, 'ELEC-011', 1, 52, 3), (12, 'ELEC-012', 1, 145, 5),
(13, 'ELEC-013', 1, 38, 2), (14, 'ELEC-014', 1, 33, 2), (15, 'ELEC-015', 1, 14, 1),
(16, 'ELEC-016', 1, 87, 3), (17, 'ELEC-017', 1, 48, 2), (18, 'ELEC-018', 1, 148, 2),
(19, 'ELEC-019', 1, 195, 5), (20, 'CLOT-001', 1, 195, 5), (21, 'CLOT-002', 1, 145, 5),
(22, 'CLOT-003', 1, 175, 5), (23, 'CLOT-004', 1, 85, 5), (24, 'CLOT-005', 1, 245, 5),
(25, 'CLOT-006', 1, 115, 5), (26, 'CLOT-007', 1, 195, 5), (27, 'CLOT-008', 1, 290, 10),
(28, 'CLOT-009', 1, 175, 5), (29, 'CLOT-010', 1, 245, 5), (30, 'CLOT-011', 1, 75, 5),
(31, 'CLOT-012', 1, 95, 5), (32, 'CLOT-013', 1, 145, 5), (33, 'CLOT-014', 1, 65, 5),
(34, 'CLOT-015', 1, 115, 5), (35, 'CLOT-016', 1, 95, 5), (36, 'CLOT-017', 1, 115, 5),
(37, 'HOME-001', 1, 38, 2), (38, 'HOME-002', 1, 33, 2), (39, 'HOME-003', 1, 115, 5),
(40, 'HOME-004', 1, 75, 5), (41, 'HOME-005', 1, 95, 5), (42, 'HOME-006', 1, 38, 2),
(43, 'HOME-007', 1, 33, 2), (44, 'HOME-008', 1, 43, 2), (45, 'HOME-009', 1, 65, 5),
(46, 'HOME-010', 1, 95, 5), (47, 'HOME-011', 1, 55, 5), (48, 'HOME-012', 1, 48, 2),
(49, 'HOME-013', 1, 43, 2), (50, 'HOME-014', 1, 75, 5),

-- Products 51-90 → Secondary Warehouse (id = 2)
(51, 'HOME-015', 2, 95, 5), (52, 'HOME-016', 2, 28, 2), (53, 'HOME-017', 2, 95, 5),
(54, 'BOOK-001', 2, 495, 5), (55, 'BOOK-002', 2, 395, 5), (56, 'BOOK-003', 2, 595, 5),
(57, 'BOOK-004', 2, 445, 5), (58, 'BOOK-005', 2, 295, 5), (59, 'BOOK-006', 2, 345, 5),
(60, 'BOOK-007', 2, 395, 5), (61, 'BOOK-008', 2, 495, 5), (62, 'BOOK-009', 2, 595, 5),
(63, 'BOOK-010', 2, 445, 5), (64, 'BOOK-011', 2, 375, 5), (65, 'BOOK-012', 2, 415, 5),
(66, 'BOOK-013', 2, 345, 5), (67, 'BOOK-014', 2, 475, 5), (68, 'BOOK-015', 2, 395, 5),
(69, 'BOOK-016', 2, 345, 5), (70, 'BOOK-017', 2, 395, 5), (71, 'SPORT-001', 2, 48, 2),
(72, 'SPORT-002', 2, 68, 2), (73, 'SPORT-003', 2, 83, 2), (74, 'SPORT-004', 2, 58, 2),
(75, 'SPORT-005', 2, 145, 5), (76, 'SPORT-006', 2, 195, 5), (77, 'SPORT-007', 2, 58, 2),
(78, 'SPORT-008', 2, 95, 5), (79, 'SPORT-009', 2, 145, 5), (80, 'SPORT-010', 2, 115, 5),
(81, 'SPORT-011', 2, 38, 2), (82, 'SPORT-012', 2, 28, 2), (83, 'SPORT-013', 2, 48, 2),
(84, 'SPORT-014', 2, 23, 2), (85, 'SPORT-015', 2, 295, 5), (86, 'SPORT-016', 2, 68, 2),
(87, 'SPORT-017', 2, 145, 5), (88, 'SPORT-018', 2, 78, 2), (89, 'SPORT-019', 2, 38, 2),
(90, 'SPORT-020', 2, 195, 5);

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
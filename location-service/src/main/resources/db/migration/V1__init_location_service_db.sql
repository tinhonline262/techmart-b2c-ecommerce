-- =========================
-- Location Service Schema
-- =========================

-- =========================
-- Table: country
-- =========================
CREATE TABLE country (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,

                         name VARCHAR(450) NOT NULL,
                         code2 VARCHAR(3),
                         code3 VARCHAR(3),

                         is_billing_enabled BOOLEAN,
                         is_shipping_enabled BOOLEAN,
                         is_city_enabled BOOLEAN,
                         is_zip_code_enabled BOOLEAN,
                         is_district_enabled BOOLEAN,

    -- audit fields
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- Table: state_or_province
-- =========================
CREATE TABLE state_or_province (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                   code VARCHAR(255),
                                   name VARCHAR(450) NOT NULL,
                                   type VARCHAR(255),

                                   country_id BIGINT NOT NULL,

    -- audit fields
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_state_country
                                       FOREIGN KEY (country_id)
                                           REFERENCES country(id)
                                           ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_state_country_id ON state_or_province(country_id);

-- =========================
-- Table: district
-- =========================
CREATE TABLE district (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,

                          name VARCHAR(450) NOT NULL,
                          type VARCHAR(450),
                          location VARCHAR(255),

                          state_or_province_id BIGINT NOT NULL,

    -- audit fields
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_district_state
                              FOREIGN KEY (state_or_province_id)
                                  REFERENCES state_or_province(id)
                                  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_district_state_id ON district(state_or_province_id);

-- =========================
-- Table: address
-- =========================
CREATE TABLE address (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,

                         contact_name VARCHAR(450),
                         phone VARCHAR(25),
                         address_line_1 VARCHAR(450),
                         address_line_2 VARCHAR(450),
                         city VARCHAR(450),
                         zip_code VARCHAR(25),

                         district_id BIGINT NOT NULL,
                         state_or_province_id BIGINT NOT NULL,
                         country_id BIGINT NOT NULL,

    -- audit fields
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_address_district
                             FOREIGN KEY (district_id)
                                 REFERENCES district(id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT fk_address_state
                             FOREIGN KEY (state_or_province_id)
                                 REFERENCES state_or_province(id)
                                 ON DELETE RESTRICT,

                         CONSTRAINT fk_address_country
                             FOREIGN KEY (country_id)
                                 REFERENCES country(id)
                                 ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_address_country_id ON address(country_id);
CREATE INDEX idx_address_state_id ON address(state_or_province_id);
CREATE INDEX idx_address_district_id ON address(district_id);

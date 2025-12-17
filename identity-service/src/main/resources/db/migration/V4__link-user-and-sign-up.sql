ALTER TABLE user
    ADD COLUMN sign_up_id VARCHAR(36) UNIQUE;

ALTER TABLE user
    ADD CONSTRAINT fk_sign_up
        FOREIGN KEY (sign_up_id) REFERENCES sign_up(id)
            ON DELETE SET NULL;

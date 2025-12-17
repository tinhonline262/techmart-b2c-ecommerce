-- V1__init_schema.sql
CREATE TABLE permission (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(512)
);

CREATE TABLE role (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role
        FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT fk_permission
        FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);

CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    oauth_id VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_user
        FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE sign_up (
    id VARCHAR(36) PRIMARY KEY,
    ol BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    password TEXT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    current_verification_token VARCHAR(255) NOT NULL,
    expired_verification_token_date TIMESTAMP NOT NULL
);

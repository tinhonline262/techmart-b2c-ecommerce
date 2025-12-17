    -- Insert Permissions
INSERT INTO permission (id, name, description) VALUES
    ('perm-1', 'ARTICLE_READ', 'Read articles'),
    ('perm-2', 'ARTICLE_CREATE', 'Create articles'),
    ('perm-3', 'ARTICLE_UPDATE', 'Update articles'),
    ('perm-4', 'ARTICLE_DELETE', 'Delete articles');

-- Insert Roles
INSERT INTO role (id, name) VALUES
    ('role-1', 'ADMIN'),
    ('role-2', 'USER');

-- Insert Role-Permission Mapping for ADMIN
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-1', 'perm-1'),
    ('role-1', 'perm-2'),
    ('role-1', 'perm-3'),
    ('role-1', 'perm-4');

-- Insert Role-Permission Mapping for USER
INSERT INTO role_permissions (role_id, permission_id) VALUES
    ('role-2', 'perm-1');

-- Insert Admin User
INSERT INTO user (
    id, ol, created_at, username, password, email, name, deleted
) VALUES (
    'user-admin-uuid',
    0,
    NOW(),
    'admin',
    '$2a$10$l/.ntO5ngnwgnStHoW/tx.W8b5gMuON7Z54PKAWvS0UpjbUJ9AGqW', -- password: admin123 (BCrypt)
    'admin@example.com',
    'Administrator',
    FALSE
);
-- Assign ADMIN role to Admin User
INSERT INTO user_roles (user_id, role_id) VALUES
    ('user-admin-uuid', 'role-1');

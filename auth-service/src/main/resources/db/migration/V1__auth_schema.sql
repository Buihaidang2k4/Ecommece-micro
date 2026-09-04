CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    module VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    locked_reason VARCHAR(255),
    locked_at DATETIME NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE password_reset_otp (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    otp_code VARCHAR(20) NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE outbox (
    id BINARY(16) PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    payload JSON NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

INSERT INTO roles (role_name, description) VALUES
 ('ADMIN', 'Administrator'),
 ('USER', 'Default user');

INSERT INTO permissions (code, description, module) VALUES
 ('user:read', 'Read users', 'auth'),
 ('user:write', 'Write users', 'auth'),
 ('user:lock', 'Lock users', 'auth'),
 ('role:manage', 'Manage roles', 'auth'),
 ('permission:manage', 'Manage permissions', 'auth'),
 ('product:read', 'Read products', 'catalog'),
 ('product:write', 'Write products', 'catalog'),
 ('order:read', 'Read own orders', 'order'),
 ('order:read_all', 'Read all orders', 'order'),
 ('order:create', 'Create orders', 'order'),
 ('order:cancel', 'Cancel orders', 'order'),
 ('order:update_status', 'Update order status', 'order'),
 ('inventory:read', 'Read inventory', 'inventory'),
 ('inventory:adjust', 'Adjust inventory', 'inventory'),
 ('coupon:read', 'Read coupons', 'promotion'),
 ('coupon:write', 'Write coupons', 'promotion'),
 ('report:export', 'Export reports', 'report'),
 ('report:revenue', 'View revenue', 'report'),
 ('media:upload', 'Upload media', 'media'),
 ('media:delete', 'Delete media', 'media'),
 ('cart:read', 'Read cart', 'cart'),
 ('cart:write', 'Write cart', 'cart'),
 ('payment:read', 'Read payments', 'payment'),
 ('payment:confirm_cod', 'Confirm COD', 'payment');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.role_name = 'ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
  'product:read','order:read','order:create','order:cancel','cart:read','cart:write','media:upload','coupon:read'
) WHERE r.role_name = 'USER';

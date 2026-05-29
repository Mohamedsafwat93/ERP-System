-- Insert test admin user
-- Username: admin
-- Password: admin123 (will be hashed with BCrypt)

INSERT INTO users (username, email, password_hash, full_name, role, is_active, created_at, tenant_id)
VALUES (
    'admin',
    'admin@erpsystem.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/M3y1o4ZJlLfGZgf0TKhIq3DMxLx8Ey', -- BCrypted "admin123"
    'System Administrator',
    'ADMIN',
    true,
    NOW(),
    1
);

INSERT INTO users (username, email, password_hash, full_name, role, is_active, created_at, tenant_id)
VALUES (
    'cashier',
    'cashier@erpsystem.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/M3y1o4ZJlLfGZgf0TKhIq3DMxLx8Ey', -- BCrypted "admin123"
    'Cashier User',
    'CASHIER',
    true,
    NOW(),
    1
);
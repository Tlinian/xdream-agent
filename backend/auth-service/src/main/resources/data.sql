-- 初始化数据
INSERT INTO users (id, username, email, password_hash, role, enabled, account_non_expired, credentials_non_expired, account_non_locked, created_at, updated_at) 
VALUES 
('admin-user-id', 'admin', 'admin@xdream.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.l0yO0h1A6OeK8qRZS', 'ADMIN', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('test-user-id', 'testuser', 'test@xdream.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.l0yO0h1A6OeK8qRZS', 'USER', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
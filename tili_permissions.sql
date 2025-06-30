DELETE FROM permissions;
ALTER TABLE permissions AUTO_INCREMENT = 1;
DELETE FROM role_permissions;
ALTER TABLE role_permissions AUTO_INCREMENT = 1;
-- This file contains the SQL commands to insert permissions into the permissions table
-- and assign them to the default role (role_id = 1).
INSERT INTO permissions (name, description) VALUES
('transaction_view', 'Allows viewing transactions'),
('transaction_create', 'Allows creating new transactions'),
('transaction_edit', 'Allows editing existing transactions'),
('transaction_delete', 'Allows deleting transactions'),
('float_view', 'Allows viewing floats'),
('float_create', 'Allows creating new floats'),
('float_edit', 'Allows editing existing floats'),
('float_delete', 'Allows deleting floats'),
('float_transaction_view', 'Allows viewing float transactions'),
('float_transaction_create', 'Allows creating new float transactions'),
('float_transaction_edit', 'Allows editing existing float transactions'),
('float_transaction_delete', 'Allows deleting float transactions'),
('register_view', 'Allows viewing register'),
('register_create', 'Allows creating new register'),
('register_edit', 'Allows editing existing register'),
('register_delete', 'Allows deleting register'),
('till_view', 'Allows viewing till'),
('till_create', 'Allows creating new till'),
('till_edit', 'Allows editing existing till'),
('till_delete', 'Allows deleting till'),
('activity_log_view', 'Allows viewing activity log'),
('reports_view', 'Allows viewing reports'),
('roles_view', 'Allows viewing roles'),
('roles_create', 'Allows creating new roles'),
('roles_edit', 'Allows editing existing roles'),
('roles_delete', 'Allows deleting roles'),
('permissions_view', 'Allows viewing permissions'),
('permissions_create', 'Allows creating new permissions'),
('permissions_edit', 'Allows editing existing permissions'),
('permissions_delete', 'Allows deleting permissions'),
('users_account_view', 'Allows viewing users profiles'),
('users_account_create', 'Allows creating new user profiles'),
('users_account_edit', 'Allows editing existing users profiles'),
('users_account_delete', 'Allows deleting users profiles'),
('application_settings_view', 'Allows viewing settings'),
('application_settings_create', 'Allows creating new settings'),
('application_settings_edit', 'Allows editing existing settings'),
('application_settings_delete', 'Allows deleting settings');

--INSERT INTO role_permissions (role_id, permission_id, created_by, created_at)
--SELECT 1, p.id, 1
--FROM permissions p;

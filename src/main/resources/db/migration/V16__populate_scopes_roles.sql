INSERT INTO scopes (name, description) VALUES
                                           ('beers:read', 'Allows you to list and view beer details.'),
                                           ('beers:write', 'Allows you to register, update, and remove beers.'),
                                           ('breweries:read', 'Allows you to list breweries.'),
                                           ('breweries:write', 'Allows you to register, update, and remove breweries.'),
                                           ('countries:read', 'Allows you to list countries.'),
                                           ('countries:write', 'Allows you to register, update, and remove countries.'),
                                           ('styles:read', 'Allows you to list styles.'),
                                           ('styles:write', 'Allows you to register, update, and remove beer styles'),
                                           ('users:read', 'Allows you to list users.'),
                                           ('users:write', 'Allows you to register, update, and remove users.');

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_AGENT');

INSERT INTO roles_scopes (role_id, scope_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'), id FROM scopes;

INSERT INTO roles_scopes (role_id, scope_id)
SELECT (SELECT id FROM roles WHERE name = 'ROLE_AGENT'), id FROM scopes WHERE name LIKE '%:read';

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1), (2, 2);
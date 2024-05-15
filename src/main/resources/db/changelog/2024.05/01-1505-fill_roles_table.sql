-- changeset GePi:20240515-1
INSERT INTO roles (id, role_name) VALUES (nextval('public.roles_seq'), 'USER_MANAGEMENT');
INSERT INTO roles (id, role_name) VALUES (nextval('public.roles_seq'), 'ORDINARY_USER');
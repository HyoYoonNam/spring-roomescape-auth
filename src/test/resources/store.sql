INSERT INTO store (id, name) VALUES (1, '잠실점');

INSERT INTO member (id, login_id, name, password, role)
VALUES (999, 'admin@roomescape.com', '어드민', 'adminPassword', 'MANAGER');

INSERT INTO manager_store (member_id, store_id) VALUES (999, 1);

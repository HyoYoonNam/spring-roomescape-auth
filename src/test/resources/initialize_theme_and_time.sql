INSERT INTO store (id, name) VALUES (1, '잠실점');

INSERT INTO member (id, login_id, name, password, role)
VALUES (1, 'sample@sample.com', '루드비코', 'samplePassword', 'USER'),
       (2, 'admin@roomescape.com', 'admin', 'adminPassword', 'MANAGER');

INSERT INTO manager_store (member_id, store_id) VALUES (2, 1);

INSERT INTO theme (id, name, description, image_url, running_time)
VALUES (1, '공포의 병원', '버려진 정신병원에서 탈출해야 합니다.', 'https://picsum.photos/200/300', 60),
       (2, '박물관 침입', '전설의 다이아몬드를 훔쳐 나오세요.', 'https://picsum.photos/200/300', 60);



INSERT INTO reservation_time (id, start_at)
VALUES (1, '10:00'),
       (2, '11:00'),
       (3, '12:00');

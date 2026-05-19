-- 회원 데이터
INSERT INTO member (id, login_id, name, password) VALUES (1, 'user1@email.com', '유저1', 'password');
INSERT INTO member (id, login_id, name, password) VALUES (2, 'user2@email.com', '유저2', 'password');

-- 테마 데이터
INSERT INTO theme (id, name, description, image_url, running_time) VALUES (1, '테마1', '설명1', 'url1', 60);

-- 예약 시간 데이터
INSERT INTO reservation_time (id, start_at) VALUES (1, '10:00');
INSERT INTO reservation_time (id, start_at) VALUES (2, '11:00');

-- 유저1의 예약 (ID: 1)
INSERT INTO reservation (id, member_id, date, time_id, theme_id) 
VALUES (1, 1, DATEADD('DAY', 1, CURRENT_DATE), 1, 1);

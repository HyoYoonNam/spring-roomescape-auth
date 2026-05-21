-- 0. 회원 데이터 (역할 추가: USER, MANAGER)
INSERT INTO member (id, login_id, name, password, role)
VALUES (1, 'sample@sample.com', '루드비코', 'samplePassword', 'USER'),
       (2, 'brown@email.com', '브라운', 'password', 'USER'),
       (3, 'json@email.com', '제이슨', 'password', 'USER'),
       (4, 'neo@email.com', '네오', 'password', 'USER'),
       (5, 'admin@roomescape.com', '어드민', 'adminPassword', 'MANAGER'),
       (6, 'manager1@store.com', '매니저1', 'managerPassword', 'MANAGER');

-- 0.5 매장 데이터
INSERT INTO store (id, name)
VALUES (1, '잠실점'),
       (2, '강남점');

-- 0.6 매장 매니저 매핑
INSERT INTO manager_store (member_id, store_id)
VALUES (5, 1), (5, 2), -- 어드민은 두 곳 관리
       (6, 1);         -- 매니저1은 잠실점 관리

-- 1. 테마 데이터 (총 12개)
INSERT INTO theme (id, name, description, image_url, running_time)
VALUES (1, '공포의 병원', '버려진 정신병원에서 탈출해야 합니다.', 'https://picsum.photos/200/300', 60),
       (2, '박물관 침입', '전설의 다이아몬드를 훔쳐 나오세요.', 'https://picsum.photos/200/300', 60),
       (3, '셜록의 서재', '명탐정의 서재 속에 숨겨진 비밀을 찾으세요.', 'https://picsum.photos/200/300', 60),
       (4, '우주선 탈출', '산소가 떨어지기 전에 지구로 귀환해야 합니다.', 'https://picsum.photos/200/300', 60),
       (5, '심해의 비밀', '심해 3000m 아래 기지에서 벌어지는 미스터리.', 'https://picsum.photos/200/300', 60),
       (6, '서부의 무법자', '현상금 사냥꾼이 되어 무법자를 잡으세요.', 'https://picsum.photos/200/300', 60),
       (7, '마법사의 방', '금지된 마법 주문을 완성해야 합니다.', 'https://picsum.photos/200/300', 60),
       (8, '감옥 탈출', '억울한 누명을 벗고 탈옥에 성공하세요.', 'https://picsum.photos/200/300', 60),
       (9, '버려진 놀이공원', '밤마다 들리는 회전목마 소리의 진실은?', 'https://picsum.photos/200/300', 60),
       (10, '고대 이집트의 저주', '파라오의 무덤 속 트랩을 피하세요.', 'https://picsum.photos/200/300', 60),
       (11, '해적선 보물찾기', '사라진 해적왕의 보물 지도를 찾으세요.', 'https://picsum.photos/200/300', 60),
       (12, '숲속의 오두막', '안개 낀 숲속, 길을 잃은 당신 앞에 나타난 오두막.', 'https://picsum.photos/200/300', 60);

-- 2. 예약 시간 데이터
INSERT INTO reservation_time (id, start_at)
VALUES (1, '10:00'),
       (2, '11:00'),
       (3, '12:00'),
       (4, '13:00'),
       (5, '14:00'),
       (6, '15:00');

-- 3. 예약 데이터 (오늘 기준 최근 7일 이내로 동적 할당, 매장 연결)
-- 1, 2, 3, 4.
INSERT INTO reservation (id, member_id, date, time_id, theme_id, store_id)
VALUES
    -- 루드비코 (member_id: 1, 잠실점: 1) - 6건
    (1, 1, DATEADD('DAY', -7, CURRENT_DATE), 1, 1, 1),
    (2, 1, DATEADD('DAY', -6, CURRENT_DATE), 2, 1, 1),
    (3, 1, DATEADD('DAY', -5, CURRENT_DATE), 3, 1, 1),
    (4, 1, DATEADD('DAY', -4, CURRENT_DATE), 4, 1, 1),
    (5, 1, DATEADD('DAY', -3, CURRENT_DATE), 1, 1, 1),
    (6, 1, DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 1),

    -- 브라운 (member_id: 2, 잠실점: 1)
    (7, 2, DATEADD('DAY', -6, CURRENT_DATE), 2, 2, 1),
    (8, 2, DATEADD('DAY', -5, CURRENT_DATE), 3, 2, 1),
    (9, 2, DATEADD('DAY', -4, CURRENT_DATE), 4, 2, 1),
    (10, 2, DATEADD('DAY', -3, CURRENT_DATE), 1, 3, 1),
    (11, 2, DATEADD('DAY', -2, CURRENT_DATE), 2, 3, 1),
    (12, 2, DATEADD('DAY', -2, CURRENT_DATE), 3, 3, 1),

    -- 제이슨 (member_id: 3, 잠실점: 1)
    (13, 3, DATEADD('DAY', -7, CURRENT_DATE), 1, 4, 1),
    (14, 3, DATEADD('DAY', -6, CURRENT_DATE), 2, 4, 1),
    (15, 3, DATEADD('DAY', -5, CURRENT_DATE), 1, 5, 1),
    (16, 3, DATEADD('DAY', -4, CURRENT_DATE), 2, 5, 1),
    (17, 3, DATEADD('DAY', -3, CURRENT_DATE), 1, 6, 1),
    (18, 3, DATEADD('DAY', -2, CURRENT_DATE), 2, 6, 1),

    -- 네오 (member_id: 4, 강남점: 2)
    (19, 4, DATEADD('DAY', -2, CURRENT_DATE), 1, 7, 2),
    (20, 4, DATEADD('DAY', -7, CURRENT_DATE), 2, 8, 2),
    (21, 4, DATEADD('DAY', -15, CURRENT_DATE), 1, 11, 2),
    (22, 4, CURRENT_DATE, 2, 11, 2),
    (23, 1, DATEADD('DAY', 1, CURRENT_DATE), 1, 2, 1),
    (24, 1, DATEADD('DAY', 2, CURRENT_DATE), 1, 2, 1);
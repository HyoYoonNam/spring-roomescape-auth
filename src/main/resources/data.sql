-- 0. 회원 데이터 (역할 추가: USER, MANAGER)
INSERT INTO member (id, login_id, name, password, role)
VALUES (1, 'sample@sample.com', '루드비코', 'samplePassword', 'USER'),
       (2, 'admin@roomescape.com', '어드민', 'adminPassword', 'MANAGER'),
       (3, 'manager1@store.com', '매니저1', 'managerPassword', 'MANAGER'),
       (4, 'manager2@store.com', '매니저2', 'managerPassword', 'MANAGER');

-- 0.5 매장 데이터
INSERT INTO store (id, name)
VALUES (1, '잠실점'),
       (2, '강남점');

-- 0.6 매장 매니저 매핑
INSERT INTO manager_store (member_id, store_id)
VALUES (2, 1), (2, 2), -- 어드민은 두 곳 관리
       (3, 1),         -- 매니저1은 잠실점 관리
       (4, 2);         -- 매니저2는 강남점 관리

-- 1. 테마 데이터 (총 12개)
INSERT INTO theme (name, description, image_url, running_time)
VALUES
    ('공포의 병원', '버려진 정신병원에서 탈출해야 합니다.', 'https://picsum.photos/200/300', 60),
    ('박물관 침입', '전설의 다이아몬드를 훔쳐 나오세요.', 'https://picsum.photos/200/300', 60),
    ('셜록의 서재', '명탐정의 서재 속에 숨겨진 비밀을 찾으세요.', 'https://picsum.photos/200/300', 60),
    ('우주선 탈출', '산소가 떨어지기 전에 지구로 귀환해야 합니다.', 'https://picsum.photos/200/300', 60),
    ('심해의 비밀', '심해 3000m 아래 기지에서 벌어지는 미스터리.', 'https://picsum.photos/200/300', 60),
    ('서부의 무법자', '현상금 사냥꾼이 되어 무법자를 잡으세요.', 'https://picsum.photos/200/300', 60),
    ('마법사의 방', '금지된 마법 주문을 완성해야 합니다.', 'https://picsum.photos/200/300', 60),
    ('감옥 탈출', '억울한 누명을 벗고 탈옥에 성공하세요.', 'https://picsum.photos/200/300', 60),
    ('버려진 놀이공원', '밤마다 들리는 회전목마 소리의 진실은?', 'https://picsum.photos/200/300', 60),
    ('고대 이집트의 저주', '파라오의 무덤 속 트랩을 피하세요.', 'https://picsum.photos/200/300', 60),
    ('해적선 보물찾기', '사라진 해적왕의 보물 지도를 찾으세요.', 'https://picsum.photos/200/300', 60),
    ('숲속의 오두막', '안개 낀 숲속, 길을 잃은 당신 앞에 나타난 오두막.', 'https://picsum.photos/200/300', 60);

-- 2. 예약 시간 데이터
INSERT INTO reservation_time (start_at)
VALUES ('10:00'), ('11:00'), ('12:00'), ('13:00'), ('14:00'), ('15:00');

-- 3. 예약 데이터 (오늘 기준 최근 7일 이내로 동적 할당, 매장 연결)
INSERT INTO reservation (member_id, date, time_id, theme_id, store_id)
VALUES
    -- 1위: 공포의 병원 (5건, 잠실점)
    (1, DATEADD('DAY', -7, CURRENT_DATE), 1, 1, 1),
    (1, DATEADD('DAY', -6, CURRENT_DATE), 2, 1, 1),
    (1, DATEADD('DAY', -5, CURRENT_DATE), 3, 1, 1),
    (1, DATEADD('DAY', -4, CURRENT_DATE), 4, 1, 1),
    (1, DATEADD('DAY', -3, CURRENT_DATE), 1, 1, 1),

    -- 2위: 박물관 침입 (4건, 잠실점)
    (1, DATEADD('DAY', -7, CURRENT_DATE), 1, 2, 1),
    (1, DATEADD('DAY', -6, CURRENT_DATE), 2, 2, 1),
    (1, DATEADD('DAY', -5, CURRENT_DATE), 3, 2, 1),
    (1, DATEADD('DAY', -4, CURRENT_DATE), 4, 2, 1),

    -- 3위: 셜록의 서재 (3건, 잠실점)
    (1, DATEADD('DAY', -3, CURRENT_DATE), 1, 3, 1),
    (1, DATEADD('DAY', -2, CURRENT_DATE), 2, 3, 1),
    (1, DATEADD('DAY', -2, CURRENT_DATE), 3, 3, 1),

    -- 4위: 서부의 무법자 (2건, 잠실점)
    (1, DATEADD('DAY', -7, CURRENT_DATE), 1, 4, 1),
    (1, DATEADD('DAY', -6, CURRENT_DATE), 2, 4, 1),

    -- 5위: 심해의 비밀 (2건, 잠실점)
    (1, DATEADD('DAY', -5, CURRENT_DATE), 1, 5, 1),
    (1, DATEADD('DAY', -4, CURRENT_DATE), 2, 5, 1),

    -- 6위: 우주선 탈출 (2건, 잠실점)
    (1, DATEADD('DAY', -3, CURRENT_DATE), 1, 6, 1),
    (1, DATEADD('DAY', -2, CURRENT_DATE), 2, 6, 1),

    -- 7위: 감옥 탈출 (1건, 강남점)
    (1, DATEADD('DAY', -2, CURRENT_DATE), 1, 7, 2),

    -- 8위: 마법사의 방 (1건, 강남점)
    (1, DATEADD('DAY', -7, CURRENT_DATE), 2, 8, 2),

    -- 예외 케이스: 집계 범위 밖 (너무 과거이거나 오늘/미래 데이터, 잠실점)
    -- 15일 전
    (1, DATEADD('DAY', -15, CURRENT_DATE), 1, 11, 1),
    -- 오늘
    (1, CURRENT_DATE, 2, 11, 1);
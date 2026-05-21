/*DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS reservation_time;
DROP TABLE IF EXISTS theme;*/

CREATE TABLE IF NOT EXISTS member
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS store
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS manager_store
(
    member_id BIGINT NOT NULL,
    store_id  BIGINT NOT NULL,
    PRIMARY KEY (member_id, store_id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE IF NOT EXISTS reservation_time
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    start_at VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS theme
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    image_url    VARCHAR(255) NOT NULL,
    running_time BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    member_id BIGINT       NOT NULL,
    date      VARCHAR(255) NOT NULL,
    time_id   BIGINT       NOT NULL,
    theme_id  BIGINT       NOT NULL,
    store_id  BIGINT       NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    FOREIGN KEY (store_id) REFERENCES store (id),
    UNIQUE (date, time_id, theme_id, store_id)
);

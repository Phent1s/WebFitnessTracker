CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role     VARCHAR(255)        NOT NULL
);

CREATE TABLE IF NOT EXISTS workouts
(
    id               BIGINT PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    type             VARCHAR(255) NOT NULL,
    date             DATE         NOT NULL,
    duration_minutes INTEGER      NOT NULL,
    calories_burned  INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS goals
(
    id            BIGINT PRIMARY KEY,
    user_id       BIGINT         NOT NULL,
    description   VARCHAR(255)   NOT NULL,
    target_value  NUMERIC(10, 2) NOT NULL,
    current_value NUMERIC(10, 2) DEFAULT 0,
    start_date    DATE           NOT NULL,
    end_date      DATE           NOT NULL,
    is_achieved   BOOLEAN        DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO users (id, username, password, role)
VALUES (1, 'Alex', '$2a$12$dZdcj73oqPdPG5thyyMGYeV7EiiKHjaSJEobwXz1Qa9J15eoVVDaq', 'ADMIN'),  -- Пароль: alex123
       (2, 'Marina', '$2a$12$FDpIz/7OCdnhFD2.gffwoOdARZaRfKJyWqDnEDGYh.X4lmfkmoNJm', 'USER'), -- Пароль: marina123
       (3, 'John', '$2a$12$YR5ysuNW0NU1LZLdpZeK5.4QjFnMxfZGv6.5hBlYSmw45Vq0Dy1tu', 'USER'); -- Пароль: john123

INSERT INTO workouts (id, user_id, type, date, duration_minutes, calories_burned)
VALUES (1, 1, 'Running', '2025-06-10', 30, 250),
       (2, 1, 'Swimming', '2025-06-12', 45, 400),
       (3, 2, 'Cycling', '2025-06-11', 60, 500),
       (4, 3, 'Yoga', '2025-06-09', 40, 150);

INSERT INTO goals (id, user_id, description, target_value, current_value, start_date, end_date, is_achieved)
VALUES (1, 1, 'Lose 5 kg', 5, 2.5, '2025-01-23', '2025-06-23', FALSE),
       (2, 2, 'Run 100 km', 100, 75, '2025-05-10', '2025-07-10', FALSE),
       (3, 3, 'Burn 5000 calories', 5000, 3200, '2025-05-13', '2025-06-13', FALSE);

create sequence users_id_seq
    increment by 50;
alter sequence users_id_seq owner to admin;


create sequence workouts_id_seq
    increment by 50;
alter sequence workouts_id_seq owner to admin;


create sequence goals_id_seq
    increment by 50;
alter sequence goals_id_seq owner to admin;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('workouts_id_seq', (SELECT MAX(id) FROM workouts));
SELECT setval('goals_id_seq', (SELECT MAX(id) FROM goals));
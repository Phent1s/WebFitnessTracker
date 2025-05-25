CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS workouts (
                                        id BIGINT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        type VARCHAR(255) NOT NULL,
                                        date DATE NOT NULL,
                                        duration_minutes INTEGER NOT NULL,
                                        calories_burned INTEGER,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS goals (
                                     id BIGINT PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     description VARCHAR(255) NOT NULL,
                                     target_value NUMERIC(10, 2) NOT NULL,
                                     current_value NUMERIC(10, 2) DEFAULT 0,
                                     start_date DATE NOT NULL,
                                     end_date DATE NOT NULL,
                                     is_achieved BOOLEAN DEFAULT FALSE,
                                     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (id, username, password) VALUES
                                           (1, 'alex', '$2a$12$LpCMAjVKkRR9.K/pGbv4AeMd7LBuXxeNN0eeS/wcNr3k6vLmYuxQ6'), -- Пароль: 1234
                                           (2, 'maria', '$2a$12$WuKW4KJIUyY2GJmYfVYTZupio/wB6sAhuFXnN/puyqSEPWFP4s9/6'), -- Пароль: qwerty
                                           (3, 'john', '$2a$12$mxQ.QNsDzam2K.ZOBq.46edMjbTqEEw.hBDCsqHIlrJxK8PiMUVW.'); -- Пароль: pass123

INSERT INTO workouts (id ,user_id, type, date, duration_minutes, calories_burned) VALUES
                                                                                  (1, 1, 'RUNNING', '2024-06-10', 30, 250),
                                                                                  (2, 1, 'SWIMMING', '2024-06-12', 45, 400),
                                                                                  (3, 2, 'CYCLING', '2024-06-11', 60, 500),
                                                                                  (4, 3, 'YOGA', '2024-06-09', 40, 150);

INSERT INTO goals (id ,user_id, description, target_value, current_value, start_date, end_date, is_achieved) VALUES
                                                                                                             (1, 1, 'Lose 5 kg', 5, 2.5, '2024-01-01', '2024-12-31', FALSE),
                                                                                                             (2, 2, 'Run 100 km', 100, 75, '2024-05-01', '2024-07-31', FALSE),
                                                                                                             (3, 3, 'Burn 5000 calories', 5000, 3200, '2024-06-01', '2024-06-30', FALSE);

create sequence users_id_seq;
alter sequence users_id_seq owner to admin;
alter sequence users_id_seq owned by users.id;

create sequence workouts_id_seq;
alter sequence workouts_id_seq owner to admin;
alter sequence workouts_id_seq owned by workouts.id;

create sequence goals_id_seq;
alter sequence goals_id_seq owner to admin;
alter sequence goals_id_seq owned by goals.id;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('workouts_id_seq', (SELECT MAX(id) FROM workouts));
SELECT setval('goals_id_seq', (SELECT MAX(id) FROM goals));
CREATE TABLE IF NOT EXISTS users(
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(20) NOT NULL UNIQUE,
                       password VARCHAR NOT NULL,
                       name VARCHAR(20) NOT NULL,
                       surname VARCHAR(30) NOT NULL,
                       email VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles (
                      user_id BIGINT NOT NULL,
                      role VARCHAR(20) NOT NULL,
                      CONSTRAINT fk_user_roles_user
                      FOREIGN KEY (user_id) REFERENCES users(id)
                      ON DELETE CASCADE
    );

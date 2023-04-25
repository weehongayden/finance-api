CREATE
OR REPLACE FUNCTION update_datetime()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at
= NOW();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE salaries
(
    id         SERIAL PRIMARY KEY,
    user_id    INT REFERENCES users (id) NOT NULL,
    amount     NUMERIC(10, 2)            NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE banks
(
    id         SERIAL PRIMARY KEY,
    user_id    INT REFERENCES users (id) NOT NULL,
    name       VARCHAR                   NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE amounts
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR        NOT NULL,
    amount     NUMERIC(10, 2) NOT NULL,
    leftover   NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE cards
(
    id             SERIAL PRIMARY KEY,
    bank_id        INT REFERENCES banks (id)   NOT NULL,
    amount_id      INT REFERENCES amounts (id) NOT NULL,
    name           VARCHAR                     NOT NULL,
    statement_date INT                         NOT NULL,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP
);

CREATE TABLE installments
(
    id              SERIAL PRIMARY KEY,
    card_id         INT REFERENCES cards (id) NOT NULL,
    name            VARCHAR                   NOT NULL UNIQUE,
    start_date      DATE                      NOT NULL,
    end_date        DATE                      NOT NULL,
    tenure          INT                       NOT NULL,
    leftover_tenure INT                       NOT NULL,
    amount          NUMERIC(10, 2)            NOT NULL,
    price_per_month NUMERIC(10, 2)            NOT NULL,
    is_active       BOOLEAN                   NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP                          DEFAULT NOW(),
    updated_at      TIMESTAMP
);

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON salaries
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON banks
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON amounts
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON cards
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

CREATE TRIGGER trigger_table_updated
    BEFORE UPDATE
    ON installments
    FOR EACH ROW
    EXECUTE FUNCTION update_datetime();

INSERT INTO users (name, created_at, updated_at)
VALUES ('WeeHong KOH', NOW(), NOW());

INSERT INTO salaries (user_id, amount, created_at, updated_at)
VALUES (1, 8000.00, NOW(), NOW());
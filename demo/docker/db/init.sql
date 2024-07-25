DROP TABLE person IF EXISTS;
DROP TABLE engineer IF EXISTS;

CREATE TABLE person  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE TABLE engineer  (
    engineer_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(40)
);
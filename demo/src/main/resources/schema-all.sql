CREATE TABLE IF NOT EXISTS person (
    person_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS engineer (
    engineer_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40)
);

TRUNCATE TABLE person;
TRUNCATE TABLE engineer;

--INSERT INTO person (first_name, last_name)
--VALUES
--    ('John', 'Doe'),
--    ('Jane', 'Smith'),
--    ('Alice', 'Johnson'),
--    ('Bob', 'Brown'),
--    ('Emily', 'Davis');
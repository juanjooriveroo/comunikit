CREATE DATABASE auth_service;
\connect auth_service;

CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO role (name) VALUES
('USER'),
('TUTOR'),
('ADMIN');

CREATE TABLE language (
    code CHAR(2) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO language (code, name) VALUES
('es', 'Spanish'),
('en', 'English'),
('fr', 'French'),
('de', 'German'),
('pt', 'Portuguese');

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL REFERENCES role(id),
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    storage_used FLOAT DEFAULT 0,
    activated BOOLEAN NOT NULL
);

CREATE TABLE user_relation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tutor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(tutor_id, user_id),
    CHECK (tutor_id != user_id)
);
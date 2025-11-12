CREATE DATABASE auth_service;
\connect auth_service

CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO rol (nombre) VALUES
('USUARIO'),
('TUTOR'),
('ADMIN'),
('INVITADO');

CREATE TABLE idioma (
    code CHAR(2) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

INSERT INTO idioma (code, nombre) VALUES
('es', 'Español'),
('en', 'English'),
('fr', 'Français'),
('de', 'Deutsch'),
('pt', 'Portugués');

CREATE TABLE users (
    id UUID PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id INT REFERENCES rol(id),
    idioma_code CHAR(2) REFERENCES idioma(code),
    storage_used BIGINT DEFAULT 0
);
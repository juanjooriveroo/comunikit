CREATE DATABASE board_service;
\connect board_service;

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

CREATE TABLE image (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image BYTEA NOT NULL,
    mime_type VARCHAR(50) NOT NULL,
    size_bytes BIGINT NOT NULL,
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    owner_id UUID NOT NULL,
    public BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE pictogram (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    image_id UUID NOT NULL REFERENCES image(id) ON DELETE CASCADE,
    owner_id UUID NOT NULL
);
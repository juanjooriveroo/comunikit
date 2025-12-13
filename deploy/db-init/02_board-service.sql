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
    is_public BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE pictogram (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    image_id UUID NOT NULL REFERENCES image(id) ON DELETE CASCADE,
    owner_id UUID NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (name, language_code)
);

CREATE TABLE section (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image_id UUID NOT NULL REFERENCES image(id),
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    owner_id UUID NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE section_pictogram (
    section_id UUID NOT NULL REFERENCES section(id) ON DELETE CASCADE,
    pictogram_id UUID NOT NULL REFERENCES pictogram(id) ON DELETE CASCADE,
    col INTEGER NOT NULL CHECK (col >= 0 AND col < 5),
    row INTEGER NOT NULL CHECK (row >= 0 AND row < 6),
    PRIMARY KEY (section_id, col, row),
    UNIQUE (section_id, pictogram_id)
);
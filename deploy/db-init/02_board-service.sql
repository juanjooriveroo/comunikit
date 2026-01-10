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

-- Tabla de tablero
CREATE TABLE board (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    language_code CHAR(2) NOT NULL REFERENCES language(code),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (owner_id)
);

-- Relación tablero-sección con posición (grid 5x6 = 30 posiciones editables)
CREATE TABLE board_section (
    board_id UUID NOT NULL REFERENCES board(id) ON DELETE CASCADE,
    section_id UUID NOT NULL REFERENCES section(id) ON DELETE CASCADE,
    col INTEGER NOT NULL CHECK (col >= 0 AND col < 5),
    row INTEGER NOT NULL CHECK (row >= 0 AND row < 6),
    PRIMARY KEY (board_id, col, row),
    UNIQUE (board_id, section_id)
);

-- Relación tablero-pictograma con posición (pictogramas sueltos en el grid)
CREATE TABLE board_pictogram (
    board_id UUID NOT NULL REFERENCES board(id) ON DELETE CASCADE,
    pictogram_id UUID NOT NULL REFERENCES pictogram(id) ON DELETE CASCADE,
    col INTEGER NOT NULL CHECK (col >= 0 AND col < 5),
    row INTEGER NOT NULL CHECK (row >= 0 AND row < 6),
    PRIMARY KEY (board_id, col, row),
    UNIQUE (board_id, pictogram_id)
);

-- Crear tableros públicos por idioma
INSERT INTO board (id, owner_id, language_code, is_public) VALUES
('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000000', 'es', true),
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000000', 'en', true),
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000000', 'fr', true),
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000000', 'de', true),
('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000000', 'pt', true);
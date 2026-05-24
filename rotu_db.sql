CREATE DATABASE IF NOT EXISTS rotu_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE rotu_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS rutas_guardadas;
DROP TABLE IF EXISTS conexiones;
DROP TABLE IF EXISTS rutas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS estaciones;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. ESTACIONES  (nodos del grafo)
CREATE TABLE IF NOT EXISTS estaciones (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    linea       VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(200)
);

-- 2. CONEXIONES  (aristas del grafo — base para Dijkstra)
CREATE TABLE IF NOT EXISTS conexiones (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    id_origen   INT          NOT NULL,
    id_destino  INT          NOT NULL,
    tiempo      INT          NOT NULL COMMENT 'Minutos',
    distancia   DECIMAL(5,1) NOT NULL COMMENT 'Kilómetros',
    FOREIGN KEY (id_origen)  REFERENCES estaciones(id),
    FOREIGN KEY (id_destino) REFERENCES estaciones(id)
);

-- 3. RUTAS  (rutas nombradas del sistema)
CREATE TABLE IF NOT EXISTS rutas (
    id        INT           AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(150)  NOT NULL,
    origen    VARCHAR(100)  NOT NULL,
    destino   VARCHAR(100)  NOT NULL,
    paradas   INT           NOT NULL,
    tiempo    INT           NOT NULL COMMENT 'Minutos',
    distancia DECIMAL(5,1)  NOT NULL COMMENT 'Kilómetros',
    linea     VARCHAR(50)   NOT NULL,
    estado    VARCHAR(20)   NOT NULL DEFAULT 'Activa'
);

-- 4. USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id        INT          AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    apellido  VARCHAR(100) NOT NULL,
    correo    VARCHAR(100) NOT NULL UNIQUE,
    telefono  VARCHAR(20),
    ciudad    VARCHAR(100),
    documento VARCHAR(20),
    tipo      VARCHAR(20)  DEFAULT 'Regular'
);

-- 5. RUTAS GUARDADAS  (favoritos por usuario)
CREATE TABLE IF NOT EXISTS rutas_guardadas (
    id         INT      AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT      NOT NULL,
    id_ruta    INT      NOT NULL,
    fecha      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_ruta)    REFERENCES rutas(id)
);

-- Estaciones 
INSERT INTO estaciones (nombre, linea, descripcion) VALUES
('Portal Norte',  'Línea verde', 'Terminal norte del sistema'),
('Calle 100',     'Línea azul',  'Estación intermedia norte'),
('Centro',        'Línea verde', 'Estación central del sistema'),
('Soacha',        'Línea azul',  'Terminal sur del sistema'),
('Usaquén',       'Línea verde', 'Estación nororiental'),
('Av. Jiménez',   'Línea verde', 'Estación centro histórico'),
('El Dorado',     'Línea roja',  'Conexión aeropuerto'),
('La Floresta',   'Línea roja',  'Estación occidental'),
('Bosa',          'Línea azul',  'Estación suroccidental'),
('Américas',      'Línea azul',  'Estación intermedia sur'),
('Chapinero',     'Línea verde', 'Estación norte-central'),
('Suba',          'Línea verde', 'Terminal noroccidental'),
('Kennedy',       'Línea roja',  'Estación suroccidental'),
('Fontibón',      'Línea azul',  'Estación occidental'),
('El Lago',       'Línea azul',  'Estación intermedia');

-- Conexiones (aristas bidireccionales del grafo) 
INSERT INTO conexiones (id_origen, id_destino, tiempo, distancia) VALUES
(1,  3,  12, 8.5),  (3,  1,  12, 8.5),   -- Portal Norte - Centro
(1,  11, 8,  5.2),  (11, 1,  8,  5.2),   -- Portal Norte - Chapinero
(11, 3,  10, 6.1),  (3,  11, 10, 6.1),   -- Chapinero - Centro
(3,  6,  5,  2.3),  (6,  3,  5,  2.3),   -- Centro - Av. Jiménez
(5,  11, 7,  4.8),  (11, 5,  7,  4.8),   -- Usaquén - Chapinero
(11, 12, 20, 13.5), (12, 11, 20, 13.5),  -- Chapinero - Suba
(2,  4,  28, 18.2), (4,  2,  28, 18.2),  -- Calle 100 - Soacha
(2,  10, 12, 7.9),  (10, 2,  12, 7.9),   -- Calle 100 - Américas
(10, 4,  18, 11.4), (4,  10, 18, 11.4),  -- Américas - Soacha
(9,  10, 10, 6.5),  (10, 9,  10, 6.5),   -- Bosa - Américas
(7,  8,  15, 9.8),  (8,  7,  15, 9.8),   -- El Dorado - La Floresta
(7,  13, 12, 7.3),  (13, 7,  12, 7.3),   -- El Dorado - Kennedy
(13, 3,  22, 14.6), (3,  13, 22, 14.6),  -- Kennedy - Centro
(14, 15, 20, 13.0), (15, 14, 20, 13.0),  -- Fontibón - El Lago
(14, 7,  10, 6.2),  (7,  14, 10, 6.2);   -- Fontibón - El Dorado

-- Rutas nombradas 
INSERT INTO rutas (nombre, origen, destino, paradas, tiempo, distancia, linea, estado) VALUES
('Portal Norte - Centro',   'Portal Norte', 'Centro',      3,  12, 8.5,  'Línea verde', 'Activa'),
('Calle 100 - Soacha',      'Calle 100',    'Soacha',      7,  28, 18.2, 'Línea azul',  'Activa'),
('Usaquén - Av. Jiménez',   'Usaquén',      'Av. Jiménez', 5,  19, 12.4, 'Línea verde', 'Activa'),
('El Dorado - La Floresta', 'El Dorado',    'La Floresta', 4,  15, 9.8,  'Línea roja',  'Activa'),
('Bosa - Américas',         'Bosa',         'Américas',    6,  22, 14.1, 'Línea azul',  'Inactiva'),
('Chapinero - Suba',        'Chapinero',    'Suba',        8,  35, 22.7, 'Línea verde', 'Activa'),
('Kennedy - Centro',        'Kennedy',      'Centro',      9,  40, 26.3, 'Línea roja',  'Activa'),
('Fontibón - El Lago',      'Fontibón',     'El Lago',     5,  20, 13.0, 'Línea azul',  'Inactiva');

-- Usuarios ───────────────────────────────────────────────────
INSERT INTO usuarios (nombre, apellido, correo, telefono, ciudad, documento, tipo) VALUES
('Carlos',  'Rodríguez', 'carlos@email.com',  '+57 310 555 0001', 'Bogotá', '1012345678', 'Premium'),
('Nicolás', 'García',    'nicolas@email.com', '+57 311 555 0002', 'Bogotá', '1098765432', 'Regular'),
('Carol',   'Suárez',    'carol@email.com',   '+57 312 555 0003', 'Bogotá', '1087654321', 'Premium');

-- Rutas guardadas 
INSERT INTO rutas_guardadas (id_usuario, id_ruta) VALUES
(1, 1), (1, 3), (1, 7),
(2, 2), (2, 5),
(3, 1), (3, 4);

SELECT 'Base de datos ROTU creada exitosamente.' AS resultado;


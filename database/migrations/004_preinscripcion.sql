-- RF3: migración aditiva. Seleccionar antes la base correcta y respaldar.
-- No ejecuta seed ni modifica socios, actividades o inscripciones existentes.
CREATE TABLE IF NOT EXISTS preinscripcion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    email VARCHAR(150) NOT NULL,
    actividad_id BIGINT NOT NULL,
    fecha_recepcion DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_preinscripcion_actividad FOREIGN KEY (actividad_id) REFERENCES actividad(id),
    CONSTRAINT ck_preinscripcion_dni CHECK (REGEXP_LIKE(dni, '^[0-9]{7,8}$', 'c')),
    INDEX ix_preinscripcion_recepcion (fecha_recepcion, id),
    INDEX ix_preinscripcion_actividad (actividad_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

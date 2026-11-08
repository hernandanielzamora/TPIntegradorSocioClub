-- MySQL Community Server 8.4 (CHECK requiere MySQL >= 8.0.16).
-- Seleccionar una base previamente creada. No elimina tablas ni actualiza esquemas existentes.
CREATE TABLE IF NOT EXISTS socio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dni VARCHAR(8) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    estado_cuota VARCHAR(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_socio_dni UNIQUE (dni),
    CONSTRAINT ck_socio_dni CHECK (REGEXP_LIKE(dni, '^[0-9]{7,8}$', 'c')),
    CONSTRAINT ck_socio_estado CHECK (estado_cuota IN ('AL_DIA', 'MOROSO')),
    INDEX ix_socio_apellido (apellido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS actividad (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    profesor VARCHAR(100) NOT NULL,
    dias VARCHAR(50) NOT NULL,
    horario VARCHAR(5) NOT NULL,
    edad_minima INT NOT NULL,
    edad_maxima INT NOT NULL,
    cupo_maximo INT NOT NULL,
    cuota_mensual DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_actividad_edades CHECK (edad_minima >= 0 AND edad_maxima >= edad_minima),
    CONSTRAINT ck_actividad_cupo CHECK (cupo_maximo > 0),
    CONSTRAINT ck_actividad_cuota CHECK (cuota_mensual >= 0),
    CONSTRAINT ck_actividad_horario CHECK (REGEXP_LIKE(horario, '^([01]?[0-9]|2[0-3]):[0-5][0-9]$', 'c')),
    INDEX ix_actividad_disciplina (disciplina)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS inscripcion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    socio_id BIGINT NOT NULL,
    actividad_id BIGINT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    estado VARCHAR(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_inscripcion_socio FOREIGN KEY (socio_id) REFERENCES socio(id),
    CONSTRAINT fk_inscripcion_actividad FOREIGN KEY (actividad_id) REFERENCES actividad(id),
    CONSTRAINT ck_inscripcion_estado CHECK (estado IN ('ACTIVA', 'BAJA')),
    INDEX ix_inscripcion_socio_estado (socio_id, estado),
    INDEX ix_inscripcion_actividad_estado (actividad_id, estado),
    INDEX ix_inscripcion_socio_actividad_estado (socio_id, actividad_id, estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- Sin UNIQUE(socio_id, actividad_id): debe poder conservarse BAJA y reinscribirse.
-- Sin cascadas de borrado: se preserva el historial; RN1–RN5 irán en servicios.

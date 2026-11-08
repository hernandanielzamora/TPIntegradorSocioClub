-- Datos sintéticos académicos. Fecha de referencia de edades: 2026-11-07.
-- Ejecutar manualmente y sin escritores concurrentes, sobre una base de desarrollo.
-- No fija IDs ni actualiza datos existentes. Usa DNI y nombre del catálogo para resolver relaciones.
-- Si ya hay actividades homónimas, revisar antes: el nombre no tiene unicidad impuesta.
SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Fútbol infantil', 'Fútbol', 'Diego Ferreyra', 'Lun y Mié', '18:00', 6, 12, 40, 18000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Fútbol infantil'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Vóley mixto', 'Vóley', 'Laura Ceballos', 'Mar y Jue', '19:00', 13, 60, 24, 20000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Vóley mixto'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Básquet juvenil', 'Básquet', 'Martín Oviedo', 'Lun y Vie', '19:30', 12, 17, 20, 19000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Básquet juvenil'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Natación', 'Natación', 'Sofía Bustos', 'Mar y Jue', '17:00', 5, 70, 30, 26000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Natación'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Patín artístico', 'Patín', 'Carla Peralta', 'Mié y Vie', '17:30', 5, 15, 25, 21000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Patín artístico'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Ajedrez', 'Ajedrez', 'Raúl Giménez', 'Sáb', '10:00', 8, 99, 16, 9000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Ajedrez'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Funcional adultos', 'Fitness', 'Paula Luna', 'Lun, Mié y Vie', '20:30', 18, 65, 30, 22000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Funcional adultos'
WHERE existente.id IS NULL;

INSERT INTO actividad (nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual)
SELECT 'Yoga', 'Bienestar', 'Inés Arguello', 'Mar y Jue', '9:00', 16, 99, 15, 17000
FROM (SELECT 1) inicio
LEFT JOIN actividad existente ON existente.nombre = 'Yoga'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30111222', 'Juan', 'Pérez', '2001-05-15', 'socio1@example.invalid', 'PRUEBA-1', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30111222'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30222333', 'Lucas', 'Gómez', '2016-06-10', 'socio2@example.invalid', 'PRUEBA-2', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30222333'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30333444', 'Martín', 'López', '2015-08-12', 'socio3@example.invalid', 'PRUEBA-3', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30333444'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30444555', 'Nicolás', 'Fernández', '2012-04-10', 'socio4@example.invalid', 'PRUEBA-4', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30444555'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30555666', 'Federico', 'Ramírez', '2006-03-20', 'socio5@example.invalid', 'PRUEBA-5', 'MOROSO'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30555666'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '30666777', 'Agustín', 'Torres', '2006-02-15', 'socio6@example.invalid', 'PRUEBA-6', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '30666777'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000001', 'SocioPrueba01', 'Ensayo', '1963-01-15', 'socio7@example.invalid', 'PRUEBA-7', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000001'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000002', 'SocioPrueba02', 'Ensayo', '1966-01-15', 'socio8@example.invalid', 'PRUEBA-8', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000002'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000003', 'SocioPrueba03', 'Ensayo', '1969-01-15', 'socio9@example.invalid', 'PRUEBA-9', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000003'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000004', 'SocioPrueba04', 'Ensayo', '1972-01-15', 'socio10@example.invalid', 'PRUEBA-10', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000004'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000005', 'SocioPrueba05', 'Ensayo', '1975-01-15', 'socio11@example.invalid', 'PRUEBA-11', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000005'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000006', 'SocioPrueba06', 'Ensayo', '1978-01-15', 'socio12@example.invalid', 'PRUEBA-12', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000006'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000007', 'SocioPrueba07', 'Ensayo', '1981-01-15', 'socio13@example.invalid', 'PRUEBA-13', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000007'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000008', 'SocioPrueba08', 'Ensayo', '1984-01-15', 'socio14@example.invalid', 'PRUEBA-14', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000008'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000009', 'SocioPrueba09', 'Ensayo', '1987-01-15', 'socio15@example.invalid', 'PRUEBA-15', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000009'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000010', 'SocioPrueba10', 'Ensayo', '1990-01-15', 'socio16@example.invalid', 'PRUEBA-16', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000010'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000011', 'SocioPrueba11', 'Ensayo', '1993-01-15', 'socio17@example.invalid', 'PRUEBA-17', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000011'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000012', 'SocioPrueba12', 'Ensayo', '1996-01-15', 'socio18@example.invalid', 'PRUEBA-18', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000012'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000013', 'SocioPrueba13', 'Ensayo', '1999-01-15', 'socio19@example.invalid', 'PRUEBA-19', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000013'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000014', 'SocioPrueba14', 'Ensayo', '2002-01-15', 'socio20@example.invalid', 'PRUEBA-20', 'AL_DIA'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000014'
WHERE existente.id IS NULL;

INSERT INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota)
SELECT '70000015', 'SocioPrueba15', 'Ensayo', '2005-01-15', 'socio21@example.invalid', 'PRUEBA-21', 'MOROSO'
FROM (SELECT 1) inicio
LEFT JOIN socio existente ON existente.dni = '70000015'
WHERE existente.id IS NULL;

-- No recrea vínculos existentes, ni siquiera si luego se dieron de baja.
INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-03-02', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '30333444' AND a.nombre = 'Fútbol infantil' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-03-05', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '30666777' AND a.nombre = 'Vóley mixto' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-03-05', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '30666777' AND a.nombre = 'Natación' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-03-09', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '30666777' AND a.nombre = 'Funcional adultos' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000001' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000002' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000003' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000004' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000005' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000006' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000007' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000008' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000009' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000010' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000011' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000012' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000013' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-11-01', 'ACTIVA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '70000014' AND a.nombre = 'Yoga' AND i.id IS NULL;

INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
SELECT s.id, a.id, '2026-03-01', 'BAJA' FROM socio s CROSS JOIN actividad a
LEFT JOIN inscripcion i ON i.socio_id = s.id AND i.actividad_id = a.id
WHERE s.dni = '30555666' AND a.nombre = 'Ajedrez' AND i.id IS NULL;

COMMIT;

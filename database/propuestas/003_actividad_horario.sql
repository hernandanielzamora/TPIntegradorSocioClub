-- MEJORA FUTURA POSTERGADA. NO ES MIGRACION APLICADA NI PARTE DEL CIERRE OBLIGATORIO.
-- PROPUESTA PREPARADA POR CODEX. NO EJECUTADA. Requiere aprobacion manual.
-- Conexion administradora NUEVA, respaldo recuperable fuera de Git y escritores detenidos.
-- MySQL 8.4. No contiene credenciales ni cambia permisos.
-- DDL genera commits implicitos: ROLLBACK no deshace estos cambios.
-- Precondiciones a declarar MANUALMENTE en la misma conexion tras aprobar:
-- SET @extra_catalogo_esperado = 'socioclub_test'; -- desarrollo solo con otra autorizacion
-- SET @extra_autorizado = 1;
SET @extra_catalogo = DATABASE();
SELECT @@hostname AS servidor,@@port AS puerto,@extra_catalogo AS catalogo,@extra_catalogo_esperado AS esperado;
SET @extra_permitido = COALESCE(@extra_autorizado=1
  AND @extra_catalogo IN ('socioclub_test','socioclub')
  AND CAST(@extra_catalogo AS BINARY)=CAST(@extra_catalogo_esperado AS BINARY),FALSE);
-- RN8: sin backfill de dias o duraciones inventadas. Guardas de estado inicial completo.
SET @extra_crear_horarios = @extra_permitido AND EXISTS (
 SELECT 1 FROM information_schema.tables WHERE table_schema=@extra_catalogo
 AND table_name='actividad' AND engine='InnoDB') AND NOT EXISTS (
 SELECT 1 FROM information_schema.tables WHERE table_schema=@extra_catalogo AND table_name='actividad_horario')
 AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema=@extra_catalogo
 AND table_name='actividad' AND column_name='horarios_confirmados');
SET @extra_sql=IF(@extra_crear_horarios,CONCAT(
 'CREATE TABLE `',@extra_catalogo,'`.actividad_horario (
 id BIGINT NOT NULL AUTO_INCREMENT,
 actividad_id BIGINT NOT NULL,
 dia_semana TINYINT NOT NULL,
 hora_inicio TIME NOT NULL,
 hora_fin TIME NOT NULL,
 PRIMARY KEY(id),
 CONSTRAINT fk_horario_actividad FOREIGN KEY(actividad_id) REFERENCES `',@extra_catalogo,'`.actividad(id),
 CONSTRAINT uk_actividad_horario UNIQUE(actividad_id,dia_semana,hora_inicio,hora_fin),
 CONSTRAINT ck_horario_dia CHECK(dia_semana BETWEEN 1 AND 7),
 CONSTRAINT ck_horario_intervalo CHECK(hora_inicio >= ''00:00:00'' AND hora_fin < ''24:00:00'' AND hora_inicio < hora_fin)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'),
 'SELECT ''NO APLICADA: revisar permiso, catalogo, tabla o columna preexistentes'' AS aviso');
PREPARE sc_extra FROM @extra_sql;
EXECUTE sc_extra;
DEALLOCATE PREPARE sc_extra;
-- Detenerse si el CREATE falla. DOS sentencias DDL no son una transaccion atomica.
SET @extra_sql=IF(@extra_crear_horarios,CONCAT(
 'ALTER TABLE `',@extra_catalogo,'`.actividad ADD COLUMN horarios_confirmados BOOLEAN NOT NULL DEFAULT FALSE,
 ADD CONSTRAINT ck_actividad_horarios_confirmados CHECK(horarios_confirmados IN (0,1))'),
 'SELECT ''ALTER NO APLICADO: revisar estado previo'' AS aviso');
PREPARE sc_extra FROM @extra_sql;
EXECUTE sc_extra;
DEALLOCATE PREPARE sc_extra;
-- No INSERT de intervalos, UPDATE de actividades ni alteracion de inscripciones.
-- Las horas fin deben definirse y aprobarse por actividad y dia antes de habilitar RN8.
SELECT column_name,column_type,is_nullable,column_default FROM information_schema.columns
WHERE table_schema=@extra_catalogo AND table_name='actividad' AND column_name='horarios_confirmados';
SELECT constraint_name,constraint_type,enforced FROM information_schema.table_constraints
WHERE constraint_schema=@extra_catalogo AND table_name='actividad_horario';
SET @extra_autorizado=NULL, @extra_catalogo_esperado=NULL;

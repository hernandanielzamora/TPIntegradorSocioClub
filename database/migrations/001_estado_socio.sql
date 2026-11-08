-- PREPARADO PARA REVISION. NO EJECUTADO POR CODEX.
-- MySQL 8.4. Respaldo verificado y aplicacion detenida antes de aplicar.
-- Cuenta administradora: no ampliar permisos de socioclub_app/socioclub_tests.
-- Seleccionar UNA base desde Workbench y verificar el primer resultado.
-- No contiene USE ni migra ambas bases automaticamente. Nunca ejecutar con --force.
-- DDL implica commits implicitos: ROLLBACK NO deshace este ALTER.
-- Precondicion manual en ESTA conexion: SET @sc_catalogo_esperado = 'socioclub_test';
-- Para desarrollo usar 'socioclub' solo tras aprobar ensayo y autorizar ese catalogo.
-- Sin coincidencia exacta con DATABASE() no se ejecuta el ALTER.

SET @sc_catalogo = DATABASE();
SET @sc_socios_antes = NULL, @sc_actividades_antes = NULL,
    @sc_inscripciones_antes = NULL, @sc_morosos_antes = NULL;
SELECT @sc_catalogo AS catalogo_seleccionado, @sc_catalogo_esperado AS catalogo_esperado;
SET @sc_catalogo_valido = COALESCE(@sc_catalogo IN ('socioclub', 'socioclub_test')
    AND CAST(@sc_catalogo AS BINARY) = CAST(@sc_catalogo_esperado AS BINARY), FALSE);
SET @sc_tablas = (SELECT COUNT(*) FROM information_schema.tables
    WHERE table_schema = @sc_catalogo AND table_name IN ('socio', 'actividad', 'inscripcion')
      AND table_type = 'BASE TABLE');
SET @sc_columna = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @sc_catalogo AND table_name = 'socio' AND column_name = 'estado');
SET @sc_restriccion = (SELECT COUNT(*) FROM information_schema.table_constraints
    WHERE constraint_schema = @sc_catalogo AND table_name = 'socio'
      AND constraint_name = 'ck_socio_vigencia');
SET @sc_preparado = @sc_catalogo_valido AND @sc_tablas = 3;

-- Captura de conteos en variables de esta conexion, no en nuevas tablas.
SET @sc_sql = IF(@sc_preparado,
    'SELECT (SELECT COUNT(*) FROM socio), (SELECT COUNT(*) FROM actividad), (SELECT COUNT(*) FROM inscripcion), (SELECT COUNT(*) FROM socio WHERE estado_cuota = ''MOROSO'') INTO @sc_socios_antes, @sc_actividades_antes, @sc_inscripciones_antes, @sc_morosos_antes',
    'SELECT ''DETENERSE: catalogo o tablas no validos'' AS aviso');
PREPARE sc_migracion FROM @sc_sql;
EXECUTE sc_migracion;
DEALLOCATE PREPARE sc_migracion;
SELECT @sc_socios_antes AS socios_antes, @sc_actividades_antes AS actividades_antes,
    @sc_inscripciones_antes AS inscripciones_antes, @sc_morosos_antes AS morosos_antes;

-- Un solo ALTER aditivo. Si existe columna o constraint, no modifica nada.
-- Una segunda ejecucion NO reactiva socios ni fuerza definiciones existentes.
SET @sc_aplicar = @sc_preparado AND @sc_columna = 0 AND @sc_restriccion = 0;
SET @sc_sql = IF(@sc_aplicar,
    'ALTER TABLE socio ADD COLUMN estado VARCHAR(8) NOT NULL DEFAULT ''ACTIVO'', ADD CONSTRAINT ck_socio_vigencia CHECK (CAST(estado AS BINARY) IN (''ACTIVO'', ''INACTIVO''))',
    'SELECT ''NO APLICADA: verificar catalogo, tablas y columna/constraint existentes; no asumir migracion correcta'' AS aviso');
PREPARE sc_migracion FROM @sc_sql;
EXECUTE sc_migracion;
DEALLOCATE PREPARE sc_migracion;

-- Inspeccion de definicion efectiva, tambien cuando se omite el ALTER.
SELECT column_name, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = @sc_catalogo AND table_name = 'socio' AND column_name = 'estado';
SELECT tc.constraint_name, cc.check_clause, tc.enforced
FROM information_schema.table_constraints tc
JOIN information_schema.check_constraints cc
  ON cc.constraint_schema = tc.constraint_schema AND cc.constraint_name = tc.constraint_name
WHERE tc.constraint_schema = @sc_catalogo AND tc.table_name = 'socio'
  AND tc.constraint_name = 'ck_socio_vigencia';

SET @sc_columna_despues = (SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = @sc_catalogo AND table_name = 'socio' AND column_name = 'estado');
SET @sc_sql = IF(@sc_preparado AND @sc_columna_despues = 1,
    'SELECT (SELECT COUNT(*) FROM socio) = @sc_socios_antes AS socios_conservados, (SELECT COUNT(*) FROM actividad) = @sc_actividades_antes AS actividades_conservadas, (SELECT COUNT(*) FROM inscripcion) = @sc_inscripciones_antes AS inscripciones_conservadas, (SELECT COUNT(*) FROM socio WHERE estado_cuota = ''MOROSO'') = @sc_morosos_antes AS morosos_conservados',
    'SELECT ''DETENERSE: no es posible verificar el resultado'' AS aviso');
PREPARE sc_migracion FROM @sc_sql;
EXECUTE sc_migracion;
DEALLOCATE PREPARE sc_migracion;
SET @sc_sql = IF(@sc_preparado AND @sc_columna_despues = 1,
    'SELECT estado, COUNT(*) AS cantidad FROM socio GROUP BY estado',
    'SELECT ''Verificacion de estados no disponible'' AS aviso');
PREPARE sc_migracion FROM @sc_sql;
EXECUTE sc_migracion;
DEALLOCATE PREPARE sc_migracion;
-- En primera aplicacion todos deben estar ACTIVO; en repeticion conservar INACTIVO.
-- Los conteos no sustituyen el respaldo ni la comparacion de datos/PK/FK originales.
-- Cada ejecucion normal requiere declarar nuevamente la intencion del operador.
SET @sc_catalogo_esperado = NULL;

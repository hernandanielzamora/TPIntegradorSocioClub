-- Fixture manual generado por Codex a partir de la alternativa aplicada por el usuario.
-- NO REEJECUTAR en el ensayo actual: los datos ya existen.
-- MySQL 8.4 / Workbench, UTF-8. Conexion nueva, backend/escritores detenidos.
-- Ejecutar por bloques con detencion ante errores. Nunca ejecutar COMMIT a ciegas.
-- No hay procedimiento, DDL ni confirmacion automatica. Todos los INSERT apuntan a testing.
USE socioclub_test;
SET NAMES utf8mb4;
SELECT @@hostname AS servidor, @@port AS puerto, DATABASE() AS catalogo, CURRENT_USER() AS cuenta;

-- BLOQUE 1: precondiciones. Si preparado=0, DETENERSE; no borrar registros para reintentar.
SET @ensayo_preparado = (
    CAST(DATABASE() AS BINARY) = CAST('socioclub_test' AS BINARY)
    AND (SELECT COUNT(*) FROM information_schema.tables
         WHERE table_schema='socioclub_test' AND table_name IN ('socio','actividad','inscripcion')
           AND engine='InnoDB' AND table_type='BASE TABLE') = 3
    AND EXISTS (SELECT 1 FROM information_schema.columns
         WHERE table_schema='socioclub_test' AND table_name='socio' AND column_name='estado'
           AND data_type='varchar' AND character_maximum_length=8 AND is_nullable='NO'
           AND CAST(column_default AS BINARY)=CAST('ACTIVO' AS BINARY))
    AND EXISTS (SELECT 1 FROM information_schema.table_constraints
         WHERE constraint_schema='socioclub_test' AND table_name='socio'
           AND constraint_name='ck_socio_vigencia' AND constraint_type='CHECK' AND enforced='YES')
    AND NOT EXISTS (SELECT 1 FROM socioclub_test.socio WHERE dni='87654321')
    AND NOT EXISTS (SELECT 1 FROM socioclub_test.actividad
         WHERE nombre IN ('Yoga Prueba Baja','Natación Prueba Baja'))
);
SELECT @ensayo_preparado AS preparado,
       IF(@ensayo_preparado=1,'CONTINUAR: precondiciones cumplidas',
          'ABORTADO: revisar catalogo, migracion o fixture preexistente; no insertar') AS resultado;

-- BLOQUE 2: solo si preparado=1. Ante CUALQUIER error: detener y ejecutar ROLLBACK.
-- Un error SQL individual NO garantiza que los INSERT anteriores se hayan revertido.
START TRANSACTION;
SET @ensayo_ok = COALESCE(@ensayo_preparado,0);
SET @ensayo_socio=NULL, @ensayo_yoga=NULL, @ensayo_natacion=NULL;
SET @ensayo_historica=NULL, @ensayo_activa_yoga=NULL, @ensayo_activa_natacion=NULL;

INSERT INTO socioclub_test.socio
(dni,nombre,apellido,fecha_nacimiento,email,telefono,estado_cuota,estado)
SELECT '87654321','Martín','Prueba Baja','1995-05-10','martin.prueba@example.com','PRUEBA-BAJA','AL_DIA','ACTIVO'
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_socio=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

INSERT INTO socioclub_test.actividad
(nombre,disciplina,profesor,dias,horario,edad_minima,edad_maxima,cupo_maximo,cuota_mensual)
SELECT 'Yoga Prueba Baja','Yoga','Docente Yoga Ficticio','Lun y Mie','10:00',18,65,5,10000.00
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_yoga=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

INSERT INTO socioclub_test.actividad
(nombre,disciplina,profesor,dias,horario,edad_minima,edad_maxima,cupo_maximo,cuota_mensual)
SELECT 'Natación Prueba Baja','Natación','Docente Natacion Ficticio','Mar y Jue','11:00',18,65,5,12000.00
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_natacion=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

INSERT INTO socioclub_test.inscripcion (socio_id,actividad_id,fecha_inscripcion,estado)
SELECT @ensayo_socio,@ensayo_yoga,DATE_SUB(CURRENT_DATE(),INTERVAL 30 DAY),'BAJA'
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_historica=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

INSERT INTO socioclub_test.inscripcion (socio_id,actividad_id,fecha_inscripcion,estado)
SELECT @ensayo_socio,@ensayo_yoga,CURRENT_DATE(),'ACTIVA'
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_activa_yoga=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

INSERT INTO socioclub_test.inscripcion (socio_id,actividad_id,fecha_inscripcion,estado)
SELECT @ensayo_socio,@ensayo_natacion,CURRENT_DATE(),'ACTIVA'
WHERE @ensayo_ok=1 AND CAST(DATABASE() AS BINARY)=CAST('socioclub_test' AS BINARY);
SET @ensayo_ok=(@ensayo_ok=1 AND ROW_COUNT()=1);
SET @ensayo_activa_natacion=IF(@ensayo_ok=1,LAST_INSERT_ID(),NULL);

-- BLOQUE 3: comprobacion dentro de la transaccion, TODAVIA SIN CONFIRMAR.
SELECT @ensayo_ok AS seis_insert_correctos, @ensayo_socio AS socio_id,
       @ensayo_yoga AS yoga_id,@ensayo_natacion AS natacion_id,
       @ensayo_historica AS historica_id,@ensayo_activa_yoga AS activa_yoga_id,
       @ensayo_activa_natacion AS activa_natacion_id;
SELECT id,dni,nombre,apellido,estado_cuota,estado
FROM socioclub_test.socio WHERE id=@ensayo_socio;
SELECT id,socio_id,actividad_id,fecha_inscripcion,estado
FROM socioclub_test.inscripcion WHERE socio_id=@ensayo_socio ORDER BY id;
SELECT a.id,a.nombre,a.cupo_maximo,
       SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS activas,
       a.cupo_maximo-SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS disponibles
FROM socioclub_test.actividad a
LEFT JOIN socioclub_test.inscripcion i ON i.actividad_id=a.id
WHERE a.id IN (@ensayo_yoga,@ensayo_natacion)
GROUP BY a.id,a.nombre,a.cupo_maximo ORDER BY a.id;
SELECT COUNT(*) AS inscripciones,SUM(estado='ACTIVA') AS activas,SUM(estado='BAJA') AS historicas
FROM socioclub_test.inscripcion WHERE socio_id=@ensayo_socio;

-- DECISION HUMANA, en ESTA MISMA conexion:
-- Si hubo error/advertencia, resultados inesperados o ensayo_ok<>1: ejecutar ROLLBACK;
-- Solo si no hubo errores/advertencias y se verifican 1 socio ACTIVO/AL_DIA,
-- 2 actividades, 3 inscripciones (2 ACTIVAS, 1 BAJA), 4 cupos libres por actividad:
-- ejecutar COMMIT;
-- COMMIT y ROLLBACK estan comentados deliberadamente: el archivo nunca confirma solo.
-- Despues del COMMIT repetir los SELECT del bloque 3 y comprobarlos desde otra conexion.
-- Tras ROLLBACK consultar DNI y nombres para verificar lo que realmente quedo;
-- los AUTO_INCREMENT pueden avanzar. No borrar ni reactivar para repetir.

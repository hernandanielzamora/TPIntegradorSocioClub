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
-- FE-ADMIN-01: tabla separada; no presupone que el administrador sea socio.
SET @extra_crear_admin = @extra_permitido AND EXISTS (
  SELECT 1 FROM information_schema.columns WHERE table_schema=@extra_catalogo
  AND table_name='socio' AND column_name='estado') AND NOT EXISTS (
  SELECT 1 FROM information_schema.tables WHERE table_schema=@extra_catalogo
  AND table_name='cuenta_administrativa');
SET @extra_sql = IF(@extra_crear_admin,CONCAT(
 'CREATE TABLE `',@extra_catalogo,'`.cuenta_administrativa (
 id BIGINT NOT NULL AUTO_INCREMENT,
 dni VARCHAR(8) NOT NULL,
 password_hash VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 rol VARCHAR(5) NOT NULL DEFAULT ''ADMIN'',
 habilitada BOOLEAN NOT NULL DEFAULT FALSE,
 PRIMARY KEY(id),
 CONSTRAINT uk_cuenta_admin_dni UNIQUE(dni),
 CONSTRAINT ck_cuenta_admin_dni CHECK(REGEXP_LIKE(dni,''^[0-9]{7,8}$'',''c'')),
 CONSTRAINT ck_cuenta_admin_rol CHECK(CAST(rol AS BINARY)=CAST(''ADMIN'' AS BINARY)),
 CONSTRAINT ck_cuenta_admin_habilitada CHECK(habilitada IN (0,1)),
 CONSTRAINT ck_cuenta_admin_hash CHECK(CHAR_LENGTH(password_hash)>=60)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci'),
 'SELECT ''NO APLICADA: falta autorizacion, catalogo/migracion incorrectos o tabla existente; revisar'' AS aviso');
PREPARE sc_extra FROM @extra_sql;
EXECUTE sc_extra;
DEALLOCATE PREPARE sc_extra;
-- Sin cuentas, hashes ni credenciales de ejemplo. Provisionamiento separado y aprobado.
-- La longitud del hash NO verifica algoritmo/fortaleza: lo hara PasswordEncoder.
SELECT column_name,column_type,is_nullable,column_default FROM information_schema.columns
WHERE table_schema=@extra_catalogo AND table_name='cuenta_administrativa' ORDER BY ordinal_position;
SET @extra_autorizado=NULL, @extra_catalogo_esperado=NULL;

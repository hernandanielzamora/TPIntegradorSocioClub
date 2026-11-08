# Plan manual de migración de vigencia de socios

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Estado: **preparado para revisión y autorización; NO ejecutado** (2026-11-08).
Rama: `feature/backend-api-rest`. Script: [001_estado_socio.sql](../database/migrations/001_estado_socio.sql).
No se ejecutó SQL, respaldo, restauración, migración, arranque autenticado ni pruebas MySQL durante esta auditoría.

## Auditoría y cambios preparados por Codex

La revisión completa del archivo encuentra un único cambio persistente: un `ALTER TABLE socio` aditivo con columna y CHECK en la misma sentencia. No contiene USE, DROP, DELETE, TRUNCATE, UPDATE de registros, cambios de permisos, FK ni índices. Las referencias a socioclub/socioclub_test son una lista de destinos admitidos, no tablas calificadas ni una migración automática de ambas bases.

Resultado previsto: `estado VARCHAR(8) NOT NULL DEFAULT 'ACTIVO'`, CHECK `ck_socio_vigencia` aplicado por defecto. Los socios anteriores reciben ACTIVO; se conservan sus otros campos y todas las actividades e inscripciones. La migración no da de baja inscripciones: eso corresponde a una futura operación funcional del servicio. La conservación efectiva debe comprobarse antes/después; no está acreditada contra las bases locales todavía.

Se reforzaron dos puntos encontrados en esta revisión:

1. La lista de dos bases no detectaba seleccionar desarrollo cuando la intención era testing. Ahora el operador debe declarar `@sc_catalogo_esperado` en la misma conexión y debe coincidir exactamente con DATABASE(). Sin declaración/coincidencia no se ejecuta el ALTER. Se limpia esa variable al terminar normalmente; declararla de nuevo incluso después de un error/reconexión.
2. El CHECK original heredaba la collation `utf8mb4_0900_ai_ci`, que podía admitir variantes de mayúsculas/acentos incompatibles con el enum Java. Ahora compara `CAST(estado AS BINARY)` para aceptar exactamente ACTIVO/INACTIVO. La misma definición quedó en schema.sql. Se ampliaron las aserciones del test SQL existente para OTRO, activo, INACTÍVO y ACTIVO con espacio final: deben producir SQLException 3819/HY000 e identificar ck_socio_vigencia. Estas nuevas aserciones **no se ejecutaron**, ni se afirma aquí su compilación posterior a la edición.

JPA coincide estáticamente: `Socio.estadoSocio` usa EnumType.STRING, tipo JDBC VARCHAR, columna estado de longitud 8 y no nula; el enum solo contiene ACTIVO/INACTIVO y el objeto nuevo inicializa ACTIVO. `estadoCuota` continúa separado en estado_cuota. Hibernate usa validate y SQL init=never: no migra automáticamente ni valida por sí solo todas las expresiones CHECK.

schema.sql sirve para instalaciones nuevas: CREATE TABLE IF NOT EXISTS no actualiza bases existentes. seed.sql omite estado en sus INSERT, usa el default para nuevos registros y no actualiza los existentes; no ejecutarlo como migración. Las fixtures JPA conservan el constructor y default ACTIVO. El test de seed incluye no reactivar INACTIVOS. Ningún índice se agrega para esta columna; se conservan PK, UNIQUE, FK e índices anteriores. No se afirma una evaluación de rendimiento de la nueva selección por estado.

Compatibilidad revisada con documentación MySQL 8.4: ALTER con ADD COLUMN/ADD CHECK, funciones deterministas en CHECK y PREPARE/EXECUTE para ALTER están soportados. La comparación binaria evita equivalencias de collation. Esto es revisión estática, no prueba de ejecución: [ALTER TABLE](https://dev.mysql.com/doc/refman/8.4/en/alter-table.html), [sentencias preparadas](https://dev.mysql.com/doc/refman/8.4/en/sql-prepared-statements.html), [CHECK](https://dev.mysql.com/doc/refman/8.4/en/create-table-check-constraints.html), [comparación binaria](https://docs.oracle.com/cd/E17952_01/mysql-8.4-en/charset-binary-set.html).

## Riesgos y condiciones de detención

- ALTER causa commits implícitos; un ROLLBACK de usuario no lo deshace. La atomicidad DDL del motor no equivale a poder revertirlo después de confirmar. [Manual MySQL](https://dev.mysql.com/doc/refman/8.4/en/implicit-commit.html).
- Puede esperar un metadata lock, bloquear escritores o reconstruir tabla. No se fuerza algoritmo/LOCK ni se promete ejecución instantánea. Detener escritores y planificar ventana, espacio disponible y respaldo recuperable.
- El script protege el nombre del catálogo, no la identidad del servidor: comprobar host, puerto y versión. Dos servidores pueden tener bases con el mismo nombre.
- Si ya existe la columna **o** el CHECK, omite el ALTER. La repetición no borra ni reactiva, pero tampoco repara una definición parcial/incorrecta. Inspeccionar y detenerse ante diferencias; no ejecutar otro ALTER improvisado.
- Los avisos DETENERSE/NO APLICADA no son SIGNAL SQL: una ejecución sin error de cliente no acredita éxito. Leer todas las salidas y el Action Output. No continuar tras errores ni usar --force.
- Los conteos no prueban identidad de los datos. Comparar también columnas anteriores, PK/FK e índices con evidencia privada anterior/posterior. No ejecutar seed, limpieza ni baja funcional entre ambas capturas.
- Exigir InnoDB, esquema previo esperado y cuentas separadas. No usar socioclub_app ni socioclub_tests para DDL ni ampliarles permisos.
- Si falta autorización, respaldo recuperable, identidad del destino o alguna comprobación: detenerse. No restaurar, quitar columnas ni borrar registros automáticamente.

## 1. Detener aplicación y obtener respaldos

En la terminal que ejecuta Spring Boot/Maven, pulsar Ctrl+C y esperar la finalización. Si se inició desde el IDE, usar su botón Stop. Cerrar también runners Postman, pruebas y otros escritores. No detener el servidor MySQL. Para investigar si sigue escuchando el puerto, sin terminar procesos a ciegas:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
    Select-Object LocalAddress, LocalPort, OwningProcess
```

En MySQL Workbench, con una conexión administradora autorizada:

1. Comprobar servidor/puerto y las consultas de identidad de la siguiente sección. Usar MySQL 8.4 y utilidades de exportación compatibles; detenerse si Workbench informa incompatibilidad o error de mysqldump.
2. Abrir Server > Data Export. Seleccionar **solo socioclub**, todas sus tablas, opción Dump Structure and Data, y Export to Self-Contained File. Incluir triggers y, si existieran, rutinas/eventos; no asumir que el dump contiene usuarios/grants del servidor.
3. Guardar fuera del repositorio, por ejemplo `C:\SocioClubBackups\2026-11-08\socioclub-antes-001.sql`. No usar .cache ni carpetas sincronizadas públicamente. Mantener acceso restringido y una copia privada separada.
4. Para permitir una restauración de ensayo en otro catálogo, dejar **Include Create Schema desmarcado**. Revisar localmente que el dump no incluya USE del origen, nombres de tablas calificados o CREATE/DROP DATABASE que redirijan una importación. No publicar contenido ni contraseñas. No modificar el respaldo original para probar una restauración.
5. Start Export y revisar Export Progress: debe finalizar sin errores y con todas las tablas. Guardar evidencia privada del resultado.
6. Repetir seleccionando **solo socioclub_test** y otro archivo `socioclub_test-antes-001.sql`; una base vacía también necesita respaldo de estructura. No sobrescribir el primero.
7. Verificar existencia, tamaño mayor que cero, fecha y hash. Inspeccionar localmente estructura y que las tablas no vacías tengan datos exportados. Cotejar los conteos anotados con una restauración de ensayo.

```powershell
Get-Item -LiteralPath 'C:\SocioClubBackups\2026-11-08\socioclub-antes-001.sql',
    'C:\SocioClubBackups\2026-11-08\socioclub_test-antes-001.sql' |
    Select-Object Name, Length, LastWriteTime
Get-FileHash -Algorithm SHA256 -LiteralPath 'C:\SocioClubBackups\2026-11-08\socioclub-antes-001.sql'
Get-FileHash -Algorithm SHA256 -LiteralPath 'C:\SocioClubBackups\2026-11-08\socioclub_test-antes-001.sql'
```

Un archivo no vacío y un hash prueban existencia/integridad del archivo, **no recuperabilidad**. Antes de autorizar la migración, realizar una restauración de ensayo autorizada en un servidor aislado, sin acceso a las bases originales: Server > Data Import > Import from Self-Contained File, seleccionar el archivo y un catálogo de recuperación vacío. Verificar primero que el SQL no redirija al origen; si lo hace, detenerse y preparar otra exportación adecuada. Comparar todas las tablas, datos, conteos y SHOW CREATE TABLE con el origen. La restauración ejecuta SQL y requiere su propia autorización; no fue realizada ni está autorizada para las bases originales. No sustituir socioclub o socioclub_test para comprobar un backup.

Referencia del procedimiento: [asistente SQL de exportación/importación de Workbench](https://dev.mysql.com/doc/workbench/en/wb-admin-export-import-management.html).

## 2. Precomprobaciones de testing (consultas para ejecución humana posterior)

Las siguientes instrucciones SQL son un plan, no evidencia de ejecución. Seleccionar socioclub_test mediante doble clic en SCHEMAS, abrir una pestaña nueva y comprobar:

```sql
SELECT @@hostname AS servidor, @@port AS puerto, VERSION() AS version,
       DATABASE() AS catalogo, CURRENT_USER() AS cuenta;
SHOW GRANTS FOR CURRENT_USER;
SELECT table_name, engine, table_collation
FROM information_schema.tables
WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'
ORDER BY table_name;
SELECT column_name, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = DATABASE() AND table_name = 'socio'
ORDER BY ordinal_position;
SELECT tc.table_name, tc.constraint_name, tc.constraint_type, tc.enforced,
       cc.check_clause
FROM information_schema.table_constraints tc
LEFT JOIN information_schema.check_constraints cc
  ON cc.constraint_schema = tc.constraint_schema
 AND cc.constraint_name = tc.constraint_name
WHERE tc.constraint_schema = DATABASE()
ORDER BY tc.table_name, tc.constraint_name;
SHOW CREATE TABLE socio;
SHOW CREATE TABLE actividad;
SHOW CREATE TABLE inscripcion;
SHOW INDEX FROM socio;
SHOW INDEX FROM actividad;
SHOW INDEX FROM inscripcion;
SELECT (SELECT COUNT(*) FROM socio) AS socios,
       (SELECT COUNT(*) FROM actividad) AS actividades,
       (SELECT COUNT(*) FROM inscripcion) AS inscripciones,
       (SELECT COUNT(*) FROM socio WHERE estado_cuota = 'MOROSO') AS morosos;
SELECT COUNT(*) AS huerfanas
FROM inscripcion i
LEFT JOIN socio s ON s.id = i.socio_id
LEFT JOIN actividad a ON a.id = i.actividad_id
WHERE s.id IS NULL OR a.id IS NULL;
```

Exigir catálogo socioclub_test, servidor previsto, cuenta administradora y tablas InnoDB conocidas. Para primera aplicación no debe existir socio.estado ni ck_socio_vigencia. Si existen, no asumir pendiente/aplicada: revisar definiciones. Para las pruebas automatizadas actuales las tres tablas de ensayo deben estar vacías; si no, detenerse **sin borrarlas** y acordar otra preparación segura. No modificar datos para que pase una prueba.

Para verificar conservación de campos anteriores, exportar los resultados completos siguientes mediante Export Resultset a CSV privados fuera de Git, con el mismo formato antes y después y sin límite de filas (no publicar datos personales). Guardar también las salidas de estructura e índices anteriores:

```sql
SELECT id, dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota
FROM socio ORDER BY id;
SELECT id, nombre, disciplina, profesor, dias, horario, edad_minima, edad_maxima,
       cupo_maximo, cuota_mensual
FROM actividad ORDER BY id;
SELECT id, socio_id, actividad_id, fecha_inscripcion, estado
FROM inscripcion ORDER BY id;
```

Los CSV anteriores/posteriores deben coincidir en valores y orden; un hash idéntico ayuda si el formato exportado es idéntico. El dump completo posterior no tendrá el mismo hash porque cambia el esquema.

## 3. Aplicar SOLO testing, después de autorización explícita

Con backup recuperable, aplicación detenida y comprobaciones aprobadas, en **la misma conexión/pestaña Workbench** seleccionada en socioclub_test:

```sql
SET @sc_catalogo_esperado = 'socioclub_test';
SELECT DATABASE() AS catalogo, @sc_catalogo_esperado AS esperado;
```

Ambos deben mostrar socioclub_test. Abrir el contenido completo de `database/migrations/001_estado_socio.sql` en esa sesión, volver a declarar la variable si Workbench usa otra conexión y ejecutar **solo ese archivo**, nunca schema.sql/seed.sql. Revisar todos los resultados/Action Output; detenerse ante errores o avisos. No ejecutar bloques sucesivos ignorando el primero. El script no contiene USE, por eso es esencial la sesión correcta.

Inmediatamente después, sin ejecutar todavía tests u operaciones funcionales:

```sql
SELECT DATABASE() AS catalogo;
SHOW CREATE TABLE socio;
SELECT column_name, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = DATABASE() AND table_name = 'socio' AND column_name = 'estado';
SELECT tc.constraint_name, tc.enforced, cc.check_clause
FROM information_schema.table_constraints tc
JOIN information_schema.check_constraints cc
  ON cc.constraint_schema = tc.constraint_schema
 AND cc.constraint_name = tc.constraint_name
WHERE tc.constraint_schema = DATABASE() AND tc.table_name = 'socio'
  AND tc.constraint_name = 'ck_socio_vigencia';
SELECT estado, COUNT(*) AS cantidad FROM socio GROUP BY estado;
SELECT COUNT(*) AS estados_invalidos
FROM socio WHERE estado IS NULL
   OR CAST(estado AS BINARY) NOT IN ('ACTIVO', 'INACTIVO');
SELECT COUNT(*) AS no_activos_primera_aplicacion
FROM socio WHERE CAST(estado AS BINARY) <> 'ACTIVO';
```

Esperar VARCHAR(8), nullable=NO, default=ACTIVO, CHECK ENFORCED=YES con comparación binaria. En primera aplicación, cero inválidos y cero no activos; si la tabla está vacía, la agrupación devuelve cero filas (no es un error). En repetición no exigir cero INACTIVOS: conservarlos. Repetir las consultas de conteos/huérfanas, estructura, índices y las tres exportaciones previas. Los indicadores de conservación del script deben valer 1. Comparar PK/FK/índices sin cambios y todos los campos anteriores idénticos. No se prueba el CHECK escribiendo datos en desarrollo: ese rechazo se valida en la suite de testing.

## 4. Pruebas autenticadas exclusivamente en testing

Desde la raíz del proyecto en la terminal PowerShell del usuario (las herramientas de Codex no heredan esas variables):

```powershell
Set-Location .\backend
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno; detenerse.' }
$env:MYSQL_TEST_ENABLED = 'true'
.\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests,InscripcionServiceMySqlTests' test
if ($LASTEXITCODE -ne 0) { throw 'Fallaron las pruebas MySQL; no migrar desarrollo.' }
.\mvnw.cmd -B verify
if ($LASTEXITCODE -ne 0) { throw 'Falló verify; no migrar desarrollo.' }
```

Si ya se está en backend, omitir Set-Location. Revisar que no existan overrides externos de datasource por argumentos/perfiles. El cargador exige socioclub/socioclub_app y socioclub_test/socioclub_tests. Las suites MySQL registran exclusivamente MYSQL_TEST_DB_URL/USERNAME/PASSWORD por DynamicPropertySource, validan la URL socioclub_test y consultan DATABASE() antes de escribir; no hay fallback a DB_*. La suite de servicio reutiliza esa configuración y exige tablas vacías. Mantener cuentas con permisos limitados al catálogo que les corresponde.

Las pruebas **sí escriben fixtures en socioclub_test**: persistencia usa rollback; integración transaccional/concurrente confirma algunos datos propios y elimina únicamente sus IDs al terminar. No son una migración ni una limpieza general. No ejecutar contra socioclub. No añadir borrados de preparación. Los AUTO_INCREMENT pueden avanzar aun si no quedan registros; no resetearlos. El esquema migrado de desarrollo no es necesario para estas pruebas, porque su datasource se reemplaza por testing.

Resultados esperados futuros, no obtenidos: 17 MySQL (7 persistencia + 10 servicio), cero fallos/errores/omisiones; verify 128 aprobadas, cero fallos/errores/omisiones. Confirmar también BUILD SUCCESS y los XML de target/surefire-reports. Si quedan omitidas, no equivale a validar MySQL. Si una falla deja fixtures, inspeccionar y detenerse sin borrar datos. Solo tras aprobación real de estas pruebas se puede considerar desarrollo.

Evidencia disponible inspeccionada en esta auditoría: XML anteriores con 128 casos, 111 aprobadas, 0 fallos, 0 errores, 17 omitidas. No acreditan el CHECK endurecido ni la migración; las nuevas aserciones siguen pendientes.

## 5. Migrar desarrollo únicamente tras aprobar testing

Requiere autorización específica para socioclub; no se infiere del permiso para testing. Detener otra vez backend/escritores, comprobar que el respaldo de desarrollo sigue vigente y recuperable (si hubo cambios, generar otro). Seleccionar **socioclub** en Workbench con la cuenta administradora. Repetir íntegramente las precomprobaciones y exportaciones de la sección 2; aquí no se exige base vacía, sino conservación exacta de los datos existentes. No usar como valores actuales los conteos históricos de 21 socios/8 actividades/19 inscripciones: medirlos.

En esa conexión, tras verificar DATABASE()=socioclub:

```sql
SET @sc_catalogo_esperado = 'socioclub';
SELECT DATABASE() AS catalogo, @sc_catalogo_esperado AS esperado;
```

Ejecutar manualmente el mismo archivo revisado, sin seed/schema ni pruebas automatizadas en esa conexión. Repetir todas las verificaciones de la sección 3 y comparar exportaciones antes/después, conservando datos, cuota, fechas e historial. En primera aplicación todos los socios deben ser ACTIVO. Si algo no coincide, mantener el backend detenido y analizar; una recuperación desde respaldo es una operación separada que necesita autorización y puede sobrescribir datos posteriores. No prometer rollback de DDL ni ejecutar una restauración automáticamente.

Solo con desarrollo migrado y validado, desde backend en una **terminal nueva normal de desarrollo**:

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno; detenerse.' }
.\mvnw.cmd spring-boot:run
```

Esperar conexión a socioclub, JPA/EntityManagerFactory y Started SocioClubApplication, sin errores validate. En otra terminal:

```powershell
(Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing).StatusCode
Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/api/actividades'
```

Son consultas; no dar de baja socios de desarrollo para comprobar el arranque. No enviar secretos o logs completos a Git.

## 6. Prueba funcional controlada de baja, SOLO testing

Es posterior a las suites, con autorización para crear/modificar datos ficticios en ensayo. Una baja no tiene reactivación implementada. No ejecutar este procedimiento contra el backend normal de desarrollo.

En una **terminal nueva dedicada**, desde backend, cargar y redirigir el datasource de ese proceso a las credenciales exclusivas de testing, sin mostrar claves ni editar .env:

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno; detenerse.' }
$env:DB_URL = $env:MYSQL_TEST_DB_URL
$env:DB_USERNAME = $env:MYSQL_TEST_DB_USERNAME
$env:DB_PASSWORD = $env:MYSQL_TEST_DB_PASSWORD
$env:PORT = '8081'
.\mvnw.cmd spring-boot:run
```

Confirmar en el log JDBC socioclub_test y puerto 8081. Solo entonces abrir `http://localhost:8081/secretaria`: crear un socio inequívocamente ficticio al día y actividades compatibles; inscribirlo mediante el flujo habitual. Si se desea probar historial previo, dar de baja una inscripción y conservar otra ACTIVA. Registrar IDs elegidos, datos, cuota, fechas y estados originales mediante SELECT en **socioclub_test**. No elegir personas de desarrollo. No se crea ningún dato en esta auditoría.

En otra terminal, introducir deliberadamente el ID de ese socio de ensayo:

```powershell
$socioEnsayoId = [long](Read-Host 'ID del socio ficticio en socioclub_test, puerto 8081')
if ($socioEnsayoId -le 0) { throw 'ID inválido.' }
$uriBaja = "http://localhost:8081/api/socios/$socioEnsayoId/baja"
Invoke-RestMethod -Method Put -Uri $uriBaja
Invoke-RestMethod -Method Put -Uri $uriBaja
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/socios/$socioEnsayoId/inscripciones"
```

Esperar ambos PUT 200/estadoSocio INACTIVO; listado de ACTIVAS vacío. En la conexión Workbench seleccionada expresamente en socioclub_test, usar el mismo ID (sustituir el 0; sin sustitución no valida nada):

```sql
SELECT DATABASE(); -- debe ser socioclub_test
SET @socio_ensayo_id = 0; -- REEMPLAZAR por el ID ficticio registrado
SELECT id, dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota, estado
FROM socioclub_test.socio WHERE id = @socio_ensayo_id;
SELECT id, socio_id, actividad_id, fecha_inscripcion, estado
FROM socioclub_test.inscripcion WHERE socio_id = @socio_ensayo_id ORDER BY id;
SELECT COUNT(*) AS activas_restantes FROM socioclub_test.inscripcion
WHERE socio_id = @socio_ensayo_id AND estado = 'ACTIVA';
```

Esperar INACTIVO, misma cuota/datos/fechas/IDs/cantidad de inscripciones, cero ACTIVAS; las que ya eran BAJA deben permanecer idénticas. Comparar con la captura antes del PUT; verificar liberación de cupo por GET /api/actividades. Probar nueva inscripción desde la API con esos IDs ficticios: esperar HTTP 409 y codigo SOCIO_INACTIVO, sin fila nueva (Postman incluye el caso). Para MVC usar otro socio ficticio ACTIVO: abrir la confirmación GET sin cambios y confirmar POST; comprobar historial. No afirmar que esta revisión visual ocurrió hasta realizarla.

Detener el backend de ensayo con Ctrl+C y cerrar esa terminal para descartar overrides. Los fixtures manuales permanecen: no se propone borrarlos ni reactivar. Las suites que exigen testing vacío no se deben repetir sobre ellos sin acordar una preparación separada y segura. Por eso se ejecutan antes de este recorrido manual. La aplicación sigue sin autenticación/autorización/CSRF: uso local, sin exposición pública.

## Trazabilidad académica y aprobación pendiente

Propuesta humana: incorporar baja lógica durante revisión de API y preservar historial en lugar de borrar. Decisión humana: aprobar diseño y exigir revisión previa a cualquier migración. Implementación y endurecimiento de script/schema/tests/plan: Codex. No se atribuyen estas ediciones a una persona ni se registra una migración, respaldo, revisión visual o prueba autenticada no realizada. AH-1/AH-2/AH-3 conservan su alcance y limitaciones respecto de la rúbrica.

El archivo está preparado para revisión y autorización de **testing primero**, sujeto a completar las condiciones anteriores. No está validado por ejecución ni autoriza automáticamente desarrollo. Registrar después fecha, operador, servidor/catálogo, backup y prueba de recuperación, hash del script aprobado, salidas previas/posteriores, Maven y decisión de continuar. En este estado no existe commit de la mejora ni de esta auditoría; no se inventan hashes.

## Confirmación humana de migraciones y validación — 2026-11-08

La necesidad de EstadoSocio surgió de la propuesta funcional humana de dar de baja socios preservando el historial. No se podía reutilizar estadoCuota: vigencia ACTIVO/INACTIVO y deuda AL_DIA/MOROSO son independientes. Codex implementó el enum, transacción de servicio, API/MVC, scripts y pruebas; no se atribuye ese código al usuario.

Secuencia comunicada por el usuario:

1. Generó manualmente respaldos independientes de socioclub_test y socioclub mediante mysqldump antes de migrar. No se recibieron ni inspeccionaron los dumps y no consta una restauración de ensayo.
2. Aplicó y validó manualmente database/migrations/001_estado_socio.sql primero en socioclub_test.
3. Ejecutó Maven verify autenticado y confirmó BUILD SUCCESS: 128 aprobadas, 0 fallos, 0 errores, 0 omitidas.
4. Posteriormente autorizó expresamente migrar socioclub y ejecutó manualmente la migración mediante MySQL Workbench; comunicó que quedó todo en orden. No se atribuye ejecución de ninguna migración a Codex.

Codex inspeccionó los diez XML actuales de backend/target/surefire-reports: 34 ApiControllerTests, 26 SecretariaControllerTests, 5 IntegridadModeloTests, 10 InscripcionServiceMySqlTests, 7 PersistenciaMySqlTests, 7 ApiConsultaServiceTests, 20 InscripcionServiceTests, 10 SecretariaServiceTests, 8 SocioBajaServiceTests y 1 SocioClubApplicationTests: 128 aprobadas, cero fallos/errores/omisiones. Los XML corroboran resultados de pruebas, no el goal verify por sí solos: BUILD SUCCESS es confirmación humana. Codex no repitió Maven ni abrió .env o conexiones SQL en esta actualización. Los 111/17 anteriores quedan como evidencia histórica.

Pendientes independientes: ejecutar/compartir la auditoría de solo lectura de socioclub (columna/default/CHECK ENFORCED, 21 socios ACTIVO, 8 actividades, 19 inscripciones, 2 morosos y FK/relaciones conservadas); no se deducen esos números de los tests en testing. Confirmar arranque y HTTP posteriores a esta migración en desarrollo; realizar el recorrido MVC/API de baja exclusivamente en testing, puerto 8081. No consta aún prueba visual, HTTP 409/SOCIO_INACTIVO manual ni ejecución de Postman. Recuperabilidad de los respaldos no demostrada por una restauración de prueba.

Procedimiento exacto y fixtures controlados: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). Incluye comparación de una BAJA histórica, dos ACTIVAS, confirmación GET sin mutación, POST, cupos, rechazo 409 e idempotencia. Los fixtures se crean solo después de verify en socioclub_test; no se borran ni reactivan. Si permanecen datos manuales, las suites que exigen testing vacío no deben ejecutarse sin acordar preparación segura. No usar desarrollo para pruebas que escriban.

Se mantienen los riesgos DDL: ALTER puede bloquear y genera commits implícitos; no se deshace con ROLLBACK. Haber aprobado testing no demuestra conservación de todos los campos en desarrollo: contrastar evidencia previa/posterior y respaldo. No reejecutar ni revertir migraciones automáticamente. Se conservan historial/fechas/vínculos y separación de vigencia y cuota como decisiones funcionales; la validación específica posterior sigue siendo necesaria.

Intervención humana adicional IH-MIG-01: respaldo, ejecución operativa de las dos migraciones y validación autenticada. Se vincula con AH-1 (credenciales de ensayo), AH-2 (configuración de desarrollo) y AH-3 (.env/cargador), conservando sus alcances y límites académicos. No se cuenta automáticamente como edición manual de código generado por IA ni se garantiza aceptación por la rúbrica. No consta un diff manual humano del SQL o Java. Esta actividad no reemplaza las tres intervenciones anteriores ni inventa ajustes de fuente.

La regresión automatizada cubre los nueve endpoints del Anexo A, MVC y RN1–RN5; la baja de socio es mejora adicional. Contrato React aditivo con estadoSocio independiente y codigo SOCIO_INACTIVO; integración Axios/React pendiente y sin cambios frontend. Sigue sin autenticación/autorización/CSRF; solo uso local. El cierre funcional/merge queda pendiente de las comprobaciones manuales preparadas. En esta actualización no se ejecutaron operaciones de datos, migraciones, bajas, commit, push ni merge.

## Cierre API y ensayo humano confirmado — 2026-11-08

El usuario confirmó carga del fixture mediante SQL convencional con transacción y COMMIT manual, y ensayo de baja correcto desde Secretaría con backend de testing en 127.0.0.1:8081. Se preservan como antecedentes los errores 1064 (near '' y luego near '//' en línea 127) al intentar el procedimiento. No existe evidencia suficiente para atribuir causa raíz definitiva al delimitador. La alternativa convencional funcionó según ejecución humana; Codex sustituyó database/fixtures/ensayo-baja-socio.sql para reflejarla, sin procedimientos/DDL, con guardas y decisión explícita COMMIT/ROLLBACK. Codex no ejecutó SQL ni alteró bases.

Pruebas completas válidas: 128 aprobadas, cero fallos/errores/omisiones, verify BUILD SUCCESS confirmado, ejecutado ANTES del fixture manual. Ahora socioclub_test contiene registros: NO repetir las suites que exigen vacío ni limpiar automáticamente. El ensayo correcto de Secretaría no demuestra por sí solo HTTP 409/SOCIO_INACTIVO, idempotencia, conservación exacta de filas, cupos finales o Postman; siguen pendientes de evidencia específica. La confirmación humana funcional es suficiente para continuar el cierre autorizado con estas limitaciones declaradas; no se inventa validación adicional.

Se mantienen IH-MIG-01 y AH-1–AH-3: migraciones/respaldos manuales y decisiones de configuración no equivalen automáticamente a ediciones de fuente. La alternativa SQL fue propuesta/aplicada por el usuario; el archivo versionable fue reescrito por Codex, no consta que el usuario lo editara. Sin autenticación/autorización/CSRF ni restauración de backups demostrada. Contrato y separación de capas en api-rest.md; fixture e instrucciones vigentes en validacion-manual-baja-socios.md. No es un despliegue público. Git se integra solo tras auditoría, sin PR y sin tocar main; hashes reales se registrarán después de ejecutarse.

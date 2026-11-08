# Validación posterior a la migración y ensayo manual de baja

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.

> Antecedente del fixture (superado por alternativa convencional; ver apartado 4): el usuario informó error 1064 al crear el procedimiento. La causa y su corrección no están comprobadas todavía. Antes de reintentar la carga, seguir la sección final «Incidente real: error 1064 al crear el auxiliar del fixture» y aportar el mensaje completo. No ejecutar CALL hasta revisar la definición.

Estado al 2026-11-08: procedimiento preparado, **no ejecutado por Codex**. El usuario confirmó respaldos independientes mediante mysqldump, migración manual primero en socioclub_test, verify 128/0/0/0 y migración posterior autorizada de socioclub mediante Workbench. No confirmó una restauración de prueba ni el recorrido funcional de baja. No repetir la migración para seguir este documento.

## 1. Auditoría de desarrollo: solo lectura, sin bajas

En Workbench conectado al servidor local previsto, ejecutar manualmente estas consultas. No seleccionan ni modifican esquemas; las referencias calificadas apuntan a socioclub. El acceso a information_schema depende de los permisos de la cuenta. Estas salidas todavía no fueron inspeccionadas por Codex:

```sql
SELECT @@hostname AS servidor, @@port AS puerto, VERSION() AS version,
       DATABASE() AS catalogo_sesion, CURRENT_USER() AS cuenta;
SELECT column_name, column_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'socioclub' AND table_name = 'socio' AND column_name = 'estado';
SELECT tc.constraint_name, tc.enforced, cc.check_clause
FROM information_schema.table_constraints tc
JOIN information_schema.check_constraints cc
  ON cc.constraint_schema = tc.constraint_schema
 AND cc.constraint_name = tc.constraint_name
WHERE tc.constraint_schema = 'socioclub' AND tc.table_name = 'socio'
  AND tc.constraint_name = 'ck_socio_vigencia';
SHOW CREATE TABLE socioclub.socio;
SHOW CREATE TABLE socioclub.inscripcion;
SELECT (SELECT COUNT(*) FROM socioclub.socio) AS socios,
       (SELECT COUNT(*) FROM socioclub.actividad) AS actividades,
       (SELECT COUNT(*) FROM socioclub.inscripcion) AS inscripciones,
       (SELECT COUNT(*) FROM socioclub.socio WHERE estado_cuota = 'MOROSO') AS morosos,
       (SELECT COUNT(*) FROM socioclub.socio WHERE CAST(estado AS BINARY) = 'ACTIVO') AS activos;
SELECT estado, COUNT(*) AS cantidad FROM socioclub.socio GROUP BY estado;
SELECT COUNT(*) AS estados_invalidos FROM socioclub.socio
WHERE estado IS NULL OR CAST(estado AS BINARY) NOT IN ('ACTIVO', 'INACTIVO');
SELECT COUNT(*) AS relaciones_huerfanas
FROM socioclub.inscripcion i
LEFT JOIN socioclub.socio s ON s.id = i.socio_id
LEFT JOIN socioclub.actividad a ON a.id = i.actividad_id
WHERE s.id IS NULL OR a.id IS NULL;
SELECT i.id, i.socio_id, s.id AS socio_encontrado,
       i.actividad_id, a.id AS actividad_encontrada, i.fecha_inscripcion, i.estado
FROM socioclub.inscripcion i
LEFT JOIN socioclub.socio s ON s.id = i.socio_id
LEFT JOIN socioclub.actividad a ON a.id = i.actividad_id
ORDER BY i.id;
SELECT constraint_name, column_name, referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'socioclub' AND table_name = 'inscripcion'
  AND referenced_table_name IS NOT NULL
ORDER BY constraint_name, ordinal_position;
```

Esperado para el estado inicial comunicado: VARCHAR(8), nullable NO, default ACTIVO, ck_socio_vigencia ENFORCED YES con CAST binario; 21 socios, 8 actividades, 19 inscripciones, 2 morosos, 21 ACTIVO, cero estados inválidos y cero relaciones huérfanas. FK hacia socio(id) y actividad(id) presentes. Si difiere, detenerse y analizar: no ajustar datos para alcanzar los números. Contar relaciones válidas no prueba que sus valores sean idénticos a los originales: cotejar con las capturas/respaldo previos. No se registra esa comparación como realizada.

## 2. Regresión autenticada antes de crear fixtures manuales

Desde backend, en PowerShell del usuario. Antes, confirmar con SELECT en socioclub_test que sus tres tablas están vacías; si no, detenerse sin borrarlas. El test de integración exige vacío y no debe adaptarse a desarrollo.

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
$env:MYSQL_TEST_ENABLED = 'true'
.\mvnw.cmd -B verify
if ($LASTEXITCODE -ne 0) { throw 'No continuar: falló verify.' }
```

Resultado ya confirmado por el usuario y corroborado en diez XML: 128 aprobadas, 0 fallos, 0 errores, 0 omitidas; BUILD SUCCESS confirmado por el usuario. Codex no repitió Maven en esta actualización. Una repetición futura debe volver a producir esos resultados. Las 17 MySQL utilizan exclusivamente las variables MYSQL_TEST_DB_* y verifican DATABASE()=socioclub_test antes de escribir. Las demás pruebas cubren contratos API/MVC, mapeo y RN1–RN5. Las fixtures automáticas son transitorias; integración elimina únicamente IDs propios. No se proponen borrados manuales ni pruebas sobre socioclub.

## 3. Instancia de ensayo dedicada en 127.0.0.1:8081

Orden del ensayo: verify (apartado 2), cargar el fixture SQL del apartado 4 con backend detenido y luego iniciar esta instancia. Detener instancias previas de ensayo con Ctrl+C y confirmar que 8081 no pertenece a otro proceso. No matar procesos a ciegas. Utilizar una terminal PowerShell NUEVA desde backend, que se cerrará al terminar. No ejecutar estas instrucciones en la terminal del backend normal:

```powershell
$puertoEnsayoOcupado = Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue
if ($puertoEnsayoOcupado) { throw '8081 está ocupado; identificar y detener la instancia anterior antes de continuar.' }
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
if ($env:MYSQL_TEST_DB_URL -cnotmatch '^jdbc:mysql://(localhost|127\.0\.0\.1):3306/socioclub_test(\?.*)?$' -or
    $env:MYSQL_TEST_DB_USERNAME -cne 'socioclub_tests' -or
    [string]::IsNullOrEmpty($env:MYSQL_TEST_DB_PASSWORD)) {
    throw 'Destino local de ensayo o cuenta de pruebas incorrectos; detenerse.'
}
$env:DB_URL = $env:MYSQL_TEST_DB_URL
$env:DB_USERNAME = $env:MYSQL_TEST_DB_USERNAME
$env:DB_PASSWORD = $env:MYSQL_TEST_DB_PASSWORD
$env:PORT = '8081'
$argumentosEnsayo = "--spring.datasource.url=$env:DB_URL --spring.datasource.username=socioclub_tests --server.port=8081 --server.address=127.0.0.1"
.\mvnw.cmd "-Dspring-boot.run.arguments=$argumentosEnsayo" spring-boot:run
```

No se muestran claves ni se pasan como argumentos. El datasource URL/usuario y puerto se fijan también por argumentos para evitar el fallback de desarrollo. No cambiar credenciales ni usar root. No añadir perfiles u otros overrides. Confirmar en el arranque JDBC socioclub_test, JPA y puerto 8081 antes de cualquier escritura; ante otra URL/puerto detener con Ctrl+C. Verificar en Workbench, con la cuenta de pruebas, SELECT DATABASE(), CURRENT_USER() y SHOW GRANTS FOR CURRENT_USER: debe usar socioclub_test, socioclub_tests y permisos exclusivos de ensayo. Esa cuenta no debe tener acceso de escritura a socioclub. No continuar si esto no se cumple. El puerto solo no demuestra la base: las garantías son datasource explícito, inspección de arranque y permisos restringidos.

En una segunda terminal para requests:

```powershell
$baseEnsayo = 'http://127.0.0.1:8081'
(Invoke-WebRequest "$baseEnsayo/" -UseBasicParsing).StatusCode
(Invoke-WebRequest "$baseEnsayo/secretaria/socios" -UseBasicParsing).StatusCode
Invoke-RestMethod "$baseEnsayo/api/actividades"
```

Esperar HTTP 200. No usar localhost:8080 para las operaciones de este ensayo. Nunca reemplazar baseEnsayo por una dirección del backend de desarrollo.

## 4. Fixture SQL convencional: solucion humana aplicada

El usuario confirmó que preparó y ejecutó manualmente el fixture con USE socioclub_test, START TRANSACTION, INSERT convencionales, LAST_INSERT_ID, consultas de verificación y COMMIT manual. La carga funcionó; después inició el backend de testing en 127.0.0.1:8081 y confirmó que el ensayo desde Secretaría salió correctamente. No consta el texto exacto de su SQL ni una edición manual del archivo del repositorio. Codex reemplazó ese archivo para reflejar la alternativa funcional comunicada.

Archivo vigente: [ensayo-baja-socio.sql](../database/fixtures/ensayo-baja-socio.sql). No usa procedimientos ni delimitadores especiales. Solo incluye seis INSERT calificados en socioclub_test, guardas de catálogo, migración e inexistencia del fixture; todos se ejecutan dentro de una transacción. Cada INSERT comprueba el catálogo y el resultado anterior. Los nombres/IDs se enlazan con LAST_INSERT_ID, sin IDs fijos, borrados, reactivaciones o actualizaciones. No ejecuta seed. No debe reejecutarse ahora: el fixture ya está cargado y se detectará como preexistente.

Para una PRIMERA carga futura, en un ensayo autorizado sin esos registros: detener escritores; abrir conexión nueva sin transacción previa; ejecutar bloque 1 y exigir preparado=1. Ejecutar bloque 2 con detención ante errores; si cualquier instrucción falla o advierte, ejecutar ROLLBACK en la misma conexión y comprobar por DNI/nombres lo que quedó. Un error SQL no implica por sí solo rollback de las sentencias anteriores. No continuar a ciegas ni confirmar desde otra conexión. Revisar bloque 3: seis_insert_correctos=1, un socio ACTIVO/AL_DIA, dos actividades, tres inscripciones (dos ACTIVAS y una BAJA histórica), cuatro cupos libres por actividad. Solo entonces el operador puede ejecutar COMMIT manualmente. Ambas decisiones están comentadas en el archivo, sin confirmación automática. Después repetir SELECT y comprobar desde una segunda conexión. No usar la cuenta administradora desde Spring Boot.

Fixture: Martín Prueba Baja, DNI 87654321, nacimiento 1995-05-10, martin.prueba@example.com, cuota AL_DIA y estado inicial ACTIVO; Yoga Prueba Baja y Natación Prueba Baja, cupo 5 y edades 18–65. Hay una BAJA histórica en Yoga y una ACTIVA en cada actividad. Los totales son del fixture, no de toda la base. Después de la baja se esperan cinco cupos por actividad, pero sus valores finales exactos siguen pendientes de evidencia.

Para observar el ensayo existente sin crear otro fixture, usar únicamente GET/SELECT. Recuperar por DNI y nombres los IDs reales para el procedimiento del apartado 5. No repetir el POST de inscripciones como preparación: solo se usará deliberadamente para verificar rechazo de INACTIVO.

```powershell
$baseEnsayo = 'http://127.0.0.1:8081'
$socioEnsayo = Invoke-RestMethod "$baseEnsayo/api/socios/dni/87654321"
$socioEnsayoId = $socioEnsayo.id
$catalogoEnsayo = @(Invoke-RestMethod "$baseEnsayo/api/actividades")
$actividadA = $catalogoEnsayo | Where-Object { $_.nombre -ceq 'Yoga Prueba Baja' }
$actividadB = $catalogoEnsayo | Where-Object { $_.nombre -ceq 'Natación Prueba Baja' }
if (@($actividadA).Count -ne 1 -or @($actividadB).Count -ne 1) { throw 'Fixture ambiguo o incompleto.' }
$cuerpoA = @{socioId=$socioEnsayoId;actividadId=$actividadA.id} | ConvertTo-Json
```

```sql
SELECT id,dni,nombre,apellido,estado_cuota,estado FROM socioclub_test.socio WHERE dni='87654321';
SELECT i.id,i.socio_id,i.actividad_id,i.fecha_inscripcion,i.estado
FROM socioclub_test.inscripcion i JOIN socioclub_test.socio s ON s.id=i.socio_id
WHERE s.dni='87654321' ORDER BY i.id;
SELECT a.id,a.nombre,a.cupo_maximo,
       SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS activas,
       a.cupo_maximo-SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS disponibles
FROM socioclub_test.actividad a LEFT JOIN socioclub_test.inscripcion i ON i.actividad_id=a.id
WHERE a.nombre IN ('Yoga Prueba Baja','Natación Prueba Baja')
GROUP BY a.id,a.nombre,a.cupo_maximo ORDER BY a.id;
```
## 5. Confirmación MVC, baja e idempotencia

1. En el listado de socios de **8081**, buscar Prueba Baja y pulsar Dar de baja. Debe abrir GET /secretaria/socios/{id}/baja con advertencia y dos inscripciones activas. **Sin confirmar**, repetir los SELECT: no debe haber cambios. Pulsar Cancelar; el socio sigue ACTIVO. Esto comprueba la confirmación del flujo visual, no un control de autorización del servidor.
2. Volver a esa pantalla y pulsar Confirmar baja del socio y sus inscripciones activas. Esperar redirección, mensaje y socio INACTIVO. Repetir SELECT: tres inscripciones, todas BAJA; los datos personales, cuota AL_DIA, IDs, fechas y vínculos permanecen. La BAJA anterior debe ser idéntica. Las dos actividades ahora tienen cinco plazas disponibles cada una (antes tenían cuatro). El historial se ve en Secretaría; GET /api/socios/{id}/inscripciones lista solo ACTIVAS y por tanto devuelve [].
3. La persona debe desaparecer del selector de nueva inscripción. Forzar el POST API con cuerpoA para comprobar la regla efectiva (ocultar opciones no es validación suficiente):

```powershell
try {
    Invoke-RestMethod -Method Post -Uri "$baseEnsayo/api/inscripciones" -ContentType 'application/json' -Body $cuerpoA
    throw 'FALLO: la inscripción de INACTIVO fue aceptada.'
} catch {
    if ($null -eq $_.Exception.Response) { throw }
    $estadoHttp = [int]$_.Exception.Response.StatusCode
    $lector = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    try { $errorApi = $lector.ReadToEnd() | ConvertFrom-Json } finally { $lector.Dispose() }
    if ($estadoHttp -ne 409 -or $errorApi.codigo -cne 'SOCIO_INACTIVO') { throw 'Respuesta inesperada.' }
    $errorApi | Select-Object error, regla, detalles, codigo
}
$repetida = Invoke-WebRequest -Method Put -Uri "$baseEnsayo/api/socios/$socioEnsayoId/baja" -UseBasicParsing
if ($repetida.StatusCode -ne 200 -or ($repetida.Content | ConvertFrom-Json).estadoSocio -cne 'INACTIVO') {
    throw 'La baja repetida no devolvió el estado esperado.'
}
```

El manejo de error anterior está pensado para Windows PowerShell 5.1. Si el cliente no entrega el cuerpo, inspeccionarlo con Postman; no declarar éxito sin comprobar 409 y codigo. Tras el rechazo y el PUT, repetir capturas: siguen tres filas BAJA, ningún alta nueva, datos/fechas/cuota intactos y cupos iguales. Es idempotencia del efecto, no obligación de devolver el mismo encabezado o instante de respuesta.

No borrar los fixtures ni reactivar socios. Son descartables por no contener datos reales, pero su retiro necesita una decisión separada; no se realiza aquí. Dejarlos documentados implica que socioclub_test ya no está vacía: no volver a ejecutar suites que exigen vacío sin acordar una preparación segura. Ejecutar verify antes de este ensayo. Detener con Ctrl+C la instancia 8081 y cerrar su terminal; no iniciar desarrollo desde una terminal con overrides de testing.

## 6. Arranque de desarrollo y Postman (resultados pendientes)

En otra terminal NUEVA normal, desde backend y después de la auditoría de solo lectura:

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
.\mvnw.cmd spring-boot:run
```

Confirmar JDBC socioclub, JPA/EntityManagerFactory, Tomcat 8080 y Started. Desde otra terminal, solo consultas:

```powershell
(Invoke-WebRequest 'http://localhost:8080/' -UseBasicParsing).StatusCode
(Invoke-WebRequest 'http://localhost:8080/secretaria/socios' -UseBasicParsing).StatusCode
Invoke-RestMethod 'http://localhost:8080/api/actividades'
```

No se consideran ejecutados estos pasos por haber pasado verify en testing. No hacer bajas ni escrituras Postman en desarrollo.

Postman: Import > Files, seleccionar postman/SocioClub.postman_collection.json y SocioClub-local.postman_environment.json. Duplicar el entorno como SocioClub testing 8081 y fijar explícitamente **baseUrl=http://127.0.0.1:8081**. Desactivar otros entornos y comprobar que ninguna variable de request/colección/runner reemplace esa URL. Configurar socioBajaId/socioId/actividadId con los IDs del fixture, no los ejemplos 1/8. Enviar individualmente las solicitudes de baja repetida y rechazo de INACTIVO, guardar status/codigo/cuerpo. No ejecutar Run collection completo: contiene DELETE y escenarios RN que requieren otros fixtures. No instalar Newman ni agregar dependencias; la colección no se ejecuta con Maven. Regresión de eliminación de actividades acreditada por tests automáticos, no por un DELETE manual en esta revisión.

Los nueve endpoints obligatorios siguen presentes: GET /actividades, GET /actividades/{id}, POST /actividades, PUT /actividades/{id}, DELETE /actividades/{id}, GET /socios/dni/{dni}, GET /socios/{id}/inscripciones, POST /inscripciones, PUT /inscripciones/{id}/baja, todos bajo /api. La baja de socio es el décimo, voluntario. MVC reutiliza servicios y RN1–RN5 siguen en InscripcionService. Evidencia automatizada: 34 API, 26 Secretaría MVC, 1 MVC raíz, 20 InscripcionService, 10 administrativos, 8 baja socio, 7 fachada API, 5 integridad y 17 MySQL = 128 aprobadas. Eso no acredita un recorrido visual ni una ejecución de la colección Postman.

React recibe DTOs explícitos sin ciclos: estadoSocio separado de estadoCuota, cupoDisponible y actividad anidada en inscripciones. Los errores mantienen error/regla/detalles y agregan codigo nullable; para INACTIVO: HTTP 409, codigo SOCIO_INACTIVO y regla null. No hay contacto personal en SocioResponse, pero DNI/nombre/nacimiento sí requieren protección de acceso. Axios/React todavía no están integrados y no se modificaron.

No hay autenticación/autorización ni protección CSRF implementada. La confirmación MVC evita errores de uso, no peticiones maliciosas; CORS no sustituye seguridad. Uso exclusivamente local, sin exposición pública. Pendiente comunicar: resultados de las consultas de desarrollo, arranque/HTTP posteriores a migración, recorrido visual completo y requests Postman de ensayo. Sin esos resultados, no declarar cierre funcional ni integrar automáticamente. No se hizo commit/push/merge.

Referencias de sintaxis revisadas para el fixture (sin ejecución): [CREATE PROCEDURE / SQL SECURITY](https://dev.mysql.com/doc/refman/8.4/en/create-procedure.html), [DECLARE HANDLER](https://dev.mysql.com/doc/refman/8.4/en/declare-handler.html). La verificación realizada es estática; el primer resultado real se registrará después de la ejecución humana.

## Incidente real: error 1064 al crear el auxiliar del fixture

El usuario informó que la ejecución manual en Workbench, conexión local puerto 3306, cuenta administradora root y catálogo socioclub_test, falló con MySQL Error 1064 durante CREATE PROCEDURE socioclub_test.fixture_ensayo_baja_socio_v1. No comunicó evidencia de INSERT exitosos y los datos no aparecen. Codex no ejecutó SQL para comprobarlo; no se afirma una carga ni un rollback observado.

Revisión estática completa: el encabezado CREATE PROCEDURE, SQL SECURITY INVOKER y MODIFIES SQL DATA están admitidos por la gramática MySQL 8.4; los DECLARE preceden a las instrucciones de sus respectivos bloques; hay un bloque exterior, otro transaccional y dos cuerpos de handler correctamente cerrados; DELIMITER $$ y DELIMITER ; rodean la definición. No se identificó una línea sintácticamente inválida comprobable con la evidencia disponible. No se atribuye el 1064 al orden de características, a DECLARE, a permisos, a la versión o a una selección parcial sin evidencia.

Diagnóstico pendiente: obtener de Action Output el mensaje completo con «near ... at line ...» y confirmar si se ejecutó todo el archivo o una selección. Un bloque enviado incompleto es una posibilidad que debe verificarse, no una causa establecida. No se aplicó una corrección SQL especulativa y no se declara solucionado el incidente. La revisión estática anterior no era una validación de compilación por el servidor; este fallo humano demuestra que esa validación de ejecución sigue pendiente.

Se conserva el auxiliar porque MySQL no revierte automáticamente todas las sentencias previas de una transacción ante cualquier error individual; los handlers garantizan el ROLLBACK de los INSERT. Quitar la rutina y dejar START TRANSACTION/INSERT/COMMIT sin un ejecutor que controle errores debilitaría la integridad pedida. Se mantienen calificación socioclub_test, detección de duplicados, seis INSERT y el mismo fixture, sin bajas ni borrados.

### Revisión humana controlada antes de otro intento

1. Guardar/copiar el error completo de Action Output, sin contraseñas. No volver a ejecutar CALL mientras se investiga el CREATE.
2. Abrir una conexión Workbench nueva sin trabajo pendiente y seleccionar socioclub_test. Comprobar SELECT DATABASE(), @@hostname, @@port y VERSION(). No usar la cuenta administradora desde Spring Boot.
3. Consultar únicamente la existencia del auxiliar y de datos del fixture con los SELECT siguientes. Si existe el auxiliar o aparecen datos, detenerse y revisar antes de reemplazarlo o repetir una carga. No eliminar nada automáticamente.
4. Para aislar la definición, en una pestaña nueva copiar desde SET NAMES utf8mb4 hasta DELIMITER ; inclusive, inmediatamente después de END //. Excluir CALL y DROP PROCEDURE. Ejecutar ese bloque completo con «Execute the selected portion ... or everything ...», conservando ambas directivas DELIMITER; no enviar un DECLARE aislado ni usar «Execute current statement» dentro del cuerpo. Revisar Action Output. Esto crearía únicamente el auxiliar, sin insertar fixtures; requiere decisión y ejecución humana. Codex no lo hizo.
5. Si vuelve el 1064, detenerse y aportar «near ... at line ...». Si CREATE termina correctamente, revisar SHOW CREATE PROCEDURE socioclub_test.fixture_ensayo_baja_socio_v1 y confirmar que coincide con el archivo revisado. Ese resultado ayudaría a diagnosticar el envío anterior, pero por sí solo no prueba exactamente qué se envió antes.
6. Solo con definición revisada, catálogo correcto y datos del fixture ausentes, ejecutar en esa misma conexión SET @sc_fixture_catalogo_invocante = DATABASE(); y luego el CALL del archivo. Revisar todas las salidas antes de retirar el auxiliar con el DROP PROCEDURE final. No ejecutar DROP TABLE ni cambiar filas. Los resultados esperados siguen siendo un socio ACTIVO/AL_DIA, dos actividades, tres inscripciones (dos ACTIVAS y una BAJA histórica) y cuatro cupos disponibles en cada actividad.

```sql
SELECT DATABASE() AS catalogo, @@hostname AS servidor, @@port AS puerto, VERSION() AS version;
SELECT routine_schema, routine_name, routine_type
FROM information_schema.routines
WHERE routine_schema = 'socioclub_test'
  AND routine_name = 'fixture_ensayo_baja_socio_v1';
SELECT id, dni, nombre, apellido, estado_cuota, estado
FROM socioclub_test.socio WHERE dni = '87654321';
SELECT id, nombre, cupo_maximo FROM socioclub_test.actividad
WHERE nombre IN ('Yoga Prueba Baja', 'Natación Prueba Baja');
SELECT i.id, i.socio_id, i.actividad_id, i.fecha_inscripcion, i.estado
FROM socioclub_test.inscripcion i
JOIN socioclub_test.socio s ON s.id = i.socio_id
WHERE s.dni = '87654321' ORDER BY i.id;
```

Referencias para la revisión: [gramática CREATE PROCEDURE](https://dev.mysql.com/doc/refman/8.4/en/create-procedure.html), [reglas DECLARE](https://dev.mysql.com/doc/refman/8.4/en/declare.html), [comandos de ejecución de Workbench](https://dev.mysql.com/doc/workbench/en/wb-sql-editor-toolbar.html). La causa raíz y la corrección efectiva permanecen pendientes del mensaje completo. No se registra una corrección de Codex como edición humana ni se atribuye a Codex el intento de ejecución.

### Evidencia ampliada y ajuste de delimitación del fixture

El usuario aportó el mensaje completo: «MySQL Error (1064) ... near '' at line 127», con siete sentencias informadas y un error. No indicó todavía si se envió todo el archivo o una selección. En el archivo previo al ajuste, CREATE comenzaba en la línea 20 y terminaba en la 146: el cierre END$$ era exactamente la línea 127 relativa al CREATE. La coincidencia es comprobable por lectura del archivo; no prueba por sí sola qué texto envió el cliente ni la causa raíz.

Interpretación limitada: el mensaje sitúa el rechazo al final del texto que el parser recibió. La hipótesis de delimitación/envío incompleto gana relevancia, pero no queda demostrada. No se identificó un DECLARE fuera de lugar ni se cambió el orden de características, permitido por la gramática. El número de sentencias reportado tampoco identifica por sí solo el programa cliente ni demuestra ejecución de CALL/INSERT.

Codex preparó un ajuste conservador de compatibilidad: DELIMITER // y cierre END // con separación explícita, manteniendo intactos el cuerpo, handlers, protección de catálogo, seis INSERT, IDs y resultados. $$ también es un delimitador válido cuando el cliente lo procesa correctamente; no se afirma que usarlo fuera un defecto de MySQL. Este cambio y el modo de envío deben validarse manualmente; no se registra el error como resuelto. No se ejecutó SQL.

Para el siguiente intento controlado, usar Workbench y una conexión nueva, consultar primero ausencia de rutina/datos con los SELECT anteriores. Si no hay auxiliar ni fixture, abrir el archivo actualizado y seleccionar desde SET NAMES utf8mb4 hasta DELIMITER ; inclusive después de END //, excluyendo CALL y DROP. Ejecutar la selección completa como script. No usar ejecutar sentencia actual dentro del cuerpo. DELIMITER es una directiva del cliente, no una sentencia que deba enviarse al servidor. Si esta fase falla, detenerse sin CALL y conservar el texto SQL efectivo que muestre el cliente junto con el error. Si la creación funciona, inspeccionar SHOW CREATE PROCEDURE socioclub_test.fixture_ensayo_baja_socio_v1; solo después, en esa misma conexión y con el catálogo correcto, ejecutar CALL del archivo y verificar las salidas. Retirar luego únicamente el auxiliar mediante el DROP PROCEDURE final. Si quedó un auxiliar de otro intento, detenerse y revisar su definición antes de retirarlo o reutilizarlo; no eliminarlo automáticamente.

Resultado esperado sin cambios: Martín Prueba Baja ACTIVO/AL_DIA, dos actividades con cupo máximo 5, una BAJA histórica en Yoga y dos ACTIVAS, una por actividad; cupo disponible 4 en ambas. La comprobación real y la causa definitiva siguen pendientes del siguiente resultado. Fuente: [ejemplo oficial de delimitación de procedimientos](https://dev.mysql.com/doc/refman/8.4/en/create-procedure.html).

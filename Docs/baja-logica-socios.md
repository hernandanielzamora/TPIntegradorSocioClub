# Baja lógica de socios — implementación y migraciones confirmadas

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Estado al 2026-11-08: el usuario aprobó el diseño y la preparación de código/scripts. Prohibió ejecutar migraciones en socioclub y socioclub_test. Esa prohibición sigue vigente. No arrancar el nuevo backend con JPA ni ejecutar tests MySQL hasta migrar el catálogo correspondiente tras una autorización posterior.

## Origen, autoría y decisión

La mejora fue propuesta personalmente por el usuario al revisar la API: advirtió que se podían gestionar inscripciones, pero no dar de baja explícitamente un socio conservando su historial. Es voluntaria: RF5 de la consigna exige alta/modificación/listado de socios; RF9 es baja de una inscripción. La comprobación manual comunicada fue JSON correcto en GET /api/actividades, anterior a esta mejora. No se afirma que el usuario haya probado esta nueva baja.

Se evaluó reutilizar estadoCuota, inferir inactividad de la ausencia de inscripciones o borrar físicamente. Se descartaron: cuota y vigencia son conceptos diferentes; un ACTIVO puede no tener inscripciones; el borrado pierde historia y compromete FK. El usuario aprobó EstadoSocio independiente, baja atómica de ACTIVAS, endpoint idempotente y confirmación MVC. Codex escribió código, migración, pruebas y documentación; no se atribuyen al usuario ediciones manuales. AH-1/AH-2/AH-3 conservan sus límites académicos.

## Modelo y comportamiento

EstadoSocio tiene ACTIVO/INACTIVO, persistido como VARCHAR(8) en socio.estado con NOT NULL, default ACTIVO y CHECK ck_socio_vigencia. No se modifica estado_cuota ni ck_socio_estado. La entidad inicializa ACTIVO y se conserva el constructor existente para que las fixtures previas sigan siendo válidas. schema.sql incorpora la definición para bases nuevas; CREATE TABLE IF NOT EXISTS no migra las actuales.

SocioService.darDeBaja abre una transacción READ_COMMITTED, bloquea el socio y consulta exclusivamente sus ACTIVAS. Bloquea sus actividades distintas en orden creciente de ID. Solo después cambia esas inscripciones a BAJA y al socio a INACTIVO. Flush detecta errores antes de finalizar; cualquier excepción técnica sin capturar provoca rollback de ambos cambios. No hay commits intermedios, SQL masivo que saltee el contexto JPA, DELETE o cascadas.

Las BAJAS anteriores no se incluyen en la consulta ni se cambian. Fecha, persona, actividad y datos personales permanecen. También se conserva estadoCuota. Repetir la operación deja INACTIVO sin reactivar ni duplicar historial; si una escritura ajena al protocolo dejó ACTIVAS de un INACTIVO, la operación las normaliza a BAJA. No se implementa reactivación.

El alta existente bloquea el mismo socio: si confirma primero, la baja posterior incluye esa inscripción; si la baja confirma primero, el alta ve INACTIVO y se rechaza. InscripcionService valida vigencia antes de RN1–RN5, incluso con AL_DIA. No se inventa una RN nueva numerada: SocioInactivoException se comunica como código SOCIO_INACTIVO. El protocolo exige que futuros escritores también respeten esos bloqueos; no se promete ausencia total de deadlocks ni seguridad frente a escrituras SQL arbitrarias.

## API y Secretaría

PUT /api/socios/{id}/baja devuelve 200 SocioResponse con estadoSocio=INACTIVO; reintento también 200. ID inválido: 400; inexistente: 404; fallo técnico: 503 genérico. PUT mantiene coherencia con la baja de inscripción y el efecto es idempotente. PATCH sería una alternativa para edición parcial general, pero no se agregan ambos contratos.

SocioResponse ahora incluye estadoSocio separado de estadoCuota. Una nueva inscripción de INACTIVO devuelve 409 con error, regla=null, detalles=[] y codigo=SOCIO_INACTIVO. Se agregó codigo nullable al envelope; error/regla/detalles anteriores permanecen. Los otros códigos RN no cambian. Contacto sigue sin exponerse en JSON. La búsqueda por DNI puede devolver un INACTIVO para consulta, sin fingir autenticación.

MVC: listado de socios muestra vigencia; un ACTIVO tiene enlace a GET /secretaria/socios/{id}/baja, una pantalla de confirmación sin mutaciones que muestra cantidad de ACTIVAS. Confirmar envía POST a la misma ruta, ejecuta el servicio y redirige con flash. La cifra de confirmación es informativa: el servicio vuelve a consultar bajo bloqueo. Listados/historial siguen accesibles; los selectores de nuevas inscripciones excluyen INACTIVOS. La edición general no acepta estadoSocio ni reactiva el registro.

React no fue modificado. Los campos nuevos son aditivos, pero la interfaz simulada no mostrará automáticamente la nueva vigencia: en su integración deberá leer estadoSocio y mostrar el error 409/codigo. El bloqueo efectivo ya reside en el servicio. No hay autenticación/autorización ni CSRF implementado; solo uso local, sin despliegue.

## Script completo y efectos

Archivo para revisión: [001_estado_socio.sql](../database/migrations/001_estado_socio.sql). Contiene guardas, conteos previos, un solo ALTER y verificaciones posteriores. No fue enviado a MySQL ni validado por ejecución en esta etapa. Está preparado para sintaxis MySQL 8.4 y debe probarse en ensayo autorizado antes de aplicarlo a desarrollo.

La única modificación propuesta por el script es:

```sql
ALTER TABLE socio
    ADD COLUMN estado VARCHAR(8) NOT NULL DEFAULT 'ACTIVO',
    ADD CONSTRAINT ck_socio_vigencia CHECK (CAST(estado AS BINARY) IN ('ACTIVO', 'INACTIVO'));
```

El script comprueba DATABASE() contra socioclub/socioclub_test, coincidencia exacta con @sc_catalogo_esperado declarado manualmente en la misma conexión y presencia de las tres tablas. Sin esa declaración no ejecuta el ALTER. Si la columna o el CHECK ya existen, no ejecuta el ALTER y muestra NO APLICADA: hay que inspeccionar definiciones, no asumir que están correctas. En segunda ejecución no borra datos ni reactiva INACTIVOS. No usa USE, DROP, DELETE, TRUNCATE o procedimientos almacenados temporales. La comparación binaria del CHECK evita valores equivalentes por collation pero incompatibles con el enum Java.

Procedimiento vigente y detallado: [plan de migración](plan-migracion-estado-socio.md), con respaldo Workbench, recuperación aislada, SQL anterior/posterior, declaración del destino, pruebas y ensayo funcional exclusivamente en testing. No ejecutar el fragmento ALTER aislado salteando las guardas del archivo completo. El endurecimiento del CHECK y sus aserciones adicionales fue preparado por Codex después del verify informado abajo; sigue pendiente de prueba MySQL.

Captura y compara cantidades de socios, actividades, inscripciones y MOROSOS; muestra tipo, nullabilidad, default y CHECK efectivo, y agrupa estados. En primera aplicación, todos los socios existentes deben quedar ACTIVO. Estos conteos no demuestran por sí solos conservación de todos los campos/PK/FK: contrastar el respaldo/estructura original. En una repetición puede haber INACTIVOS legítimos que deben permanecer.

DDL de MySQL implica commits implícitos: no envolverlo con START TRANSACTION/ROLLBACK para prometer reversión. Puede bloquear o reconstruir tabla según el servidor; programar una ventana sin escritores y revisar errores antes de reintentar. La aplicación no tiene permisos ALTER y no debe recibirlos. Se requiere la cuenta administradora para la migración manual, no para ejecutar Spring o tests.

## Aplicación controlada, solo tras autorización posterior

1. Revisar el script completo y autorizar explícitamente ejecución y catálogo. Actualmente no existe esa autorización.
2. Detener backend/escritores. Crear respaldo consistente de estructura y datos de la base elegida mediante Workbench Data Export o herramienta de backup autorizada, fuera de Git. Verificar que sea recuperable; no se hizo ese respaldo por Codex.
3. Revisar estado previo: servidor/puerto/catálogo correctos, tres tablas, columnas, CHECK, conteos, datos y PK/FK. Si ya existe estado, inspeccionar su definición y detenerse ante diferencia. No reinicializar bases.
4. Primero socioclub_test: seleccionar explícitamente el catálogo en Workbench con la cuenta administradora y verificar SELECT DATABASE(). Abrir el archivo y ejecutarlo únicamente después de la autorización. No automatiza ambos catálogos y no usar opciones que ignoren errores.
5. Guardar evidencia anterior/posterior, revisar avisos, tipos/default/CHECK y conteos. Columna VARCHAR(8), NOT NULL, default ACTIVO y CHECK aplicado. Probar que el esquema y las fixtures de ensayo funcionan; cualquier anomalía obliga a detenerse. DDL no se revierte con ROLLBACK.
6. Solo después de validar ensayo y con permiso para socioclub, repetir el mismo procedimiento con respaldo separado en desarrollo. No restaurar/inicializar/borrar automáticamente si algo falla.
7. Las cuentas normales conservan sus permisos CRUD. El código nuevo usa ddl-auto=validate: arrancará únicamente cuando la base a la que conecta tenga el nuevo esquema. schema.sql es para instalación nueva, no sustituye esta migración.

seed.sql no cambia: omite la columna, por lo que filas nuevas usan el default. Su estrategia de inserción de ausentes no actualiza INACTIVOS existentes. Se amplió su prueba de repetibilidad para comprobarlo; queda pendiente de MySQL migrado.

## Pruebas ejecutadas y pendientes

Verify offline con MySQL desactivado explícitamente: BUILD SUCCESS, compilación y JAR. 128 casos: 111 aprobados, 0 fallos, 0 errores y 17 omitidos. No se abrió .env, arrancó JPA autenticado o ejecutó una migración. Se usó la distribución Maven 3.10.0 ya instalada por el wrapper y su caché con permiso para locks fuera del sandbox.

| Suite | Aprobadas | Omitidas |
| --- | ---: | ---: |
| ApiControllerTests | 34 | 0 |
| ApiConsultaServiceTests | 7 | 0 |
| SecretariaControllerTests | 26 | 0 |
| SecretariaServiceTests | 10 | 0 |
| SocioBajaServiceTests (nueva) | 8 | 0 |
| InscripcionServiceTests | 20 | 0 |
| IntegridadModeloTests | 5 | 0 |
| SocioClubApplicationTests | 1 | 0 |
| PersistenciaMySqlTests | 0 | 7 |
| InscripcionServiceMySqlTests | 0 | 10 |
| Total | 111 | 17 |

Pruebas sin base: activo por defecto e independencia de cuota, baja múltiple y orden de bloqueo, conservación de datos/fechas/BAJA, repetición, inexistente, validación antes de mutar, propagación de fallo de flush (sin afirmar rollback de mocks), edición sin reactivación, selector sin inactivos, rechazo de INACTIVO al día, contrato REST, confirmación GET sin escritura y POST/PRG. Las regresiones previas siguen aprobando.

Tres pruebas MySQL nuevas preparadas y no ejecutadas: conservación/liberación/idempotencia con persistencia real; error SQL después del flush pero antes del commit para revertir socio e inscripciones en una transacción exterior READ_COMMITTED; y carrera alta/baja con resultado final INACTIVO sin ACTIVAS independientemente de quién gana. Se amplió el test seed para comprobar que no reactiva. Se reutilizan las guardas de socioclub_test y limpieza de IDs propios; nunca tests sobre socioclub.

Los 109 aprobados de los reportes anteriores son de la API previa, sin la nueva columna: no acreditan este modelo. Las 17 pruebas MySQL actuales no se ejecutarán antes de migrar ensayo con autorización.

## Postman y validación posterior

Colección actualizada a 22 requests para nueve endpoints obligatorios y uno voluntario. Nueva carpeta con baja, repetición, inexistente y rechazo posterior por INACTIVO. socioBajaId queda vacío: elegir deliberadamente un socio ficticio cuando la base esté migrada; la baja no se reactiva desde la app. Se actualizaron ejemplos de estadoSocio/codigo. JSON válido, sin ejecución de Postman/runner.

Antes de la migración solo pruebas sin base, desde backend:

```powershell
.\mvnw.cmd -B '-Dtest=ApiControllerTests,ApiConsultaServiceTests,SecretariaControllerTests,SecretariaServiceTests,SocioBajaServiceTests,InscripcionServiceTests,IntegridadModeloTests,SocioClubApplicationTests' test
```

Después de autorización, migración de ensayo y revisión de fixtures, no antes:

```powershell
.\scripts\load-env.ps1
if ($?) {
    .\mvnw.cmd -B verify
}
```

Resultado esperado futuro: 128 sin omisiones, pero no se marca como obtenido. Después de migrar también desarrollo, iniciar Spring Boot y recorrer confirmación/baja/historial con datos ficticios. No se hizo esa prueba manual del módulo nuevo.

## Defensa y revisión pendiente

¿Por qué no usar MOROSO para la baja? Porque deuda y vigencia son independientes. ¿Por qué conservar el historial? Evita pérdida de trazabilidad y preserva FK. ¿Qué libera el cupo? Cambiar ACTIVAS a BAJA; los conteos ya excluyen BAJA. ¿Qué asegura atomicidad? Una sola transacción de servicio sin capturar fallos técnicos; el rollback real todavía debe verificarse en MySQL. ¿Por qué la aplicación puede no arrancar ahora? validate exige la columna y no se autorizó migrarla todavía.

Puntos concretos para revisión humana: SQL/guardas y comprobaciones anteriores/posteriores; orden de bloqueos de la baja múltiple; idempotencia y contrato SOCIO_INACTIVO para React. Son revisión pendiente, no modificaciones manuales atribuidas al equipo. No está listo para integrar hasta revisar/aplicar migración y ejecutar las pruebas autenticadas. Sin commit, push o merge.

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

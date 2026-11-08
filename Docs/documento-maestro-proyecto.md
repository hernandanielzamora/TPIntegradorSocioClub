# Documento maestro — SocioClub

> Etapa vigente: backend integrado en dev (08d7c40); integración React en feature/react-integracion-api, sin merge de React. Ver react-integracion-api.md y el cierre al final. Los estados anteriores se conservan como historial.

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Actualización vigente: baja lógica de socios preparada por aprobación del usuario; ejecución de migraciones NO autorizada en ninguna base. Verify actual sin MySQL: 111 aprobadas, 0 fallos/errores, 17 omitidas. Código nuevo requiere migrar el catálogo antes de usar JPA. Script completo en database/migrations/001_estado_socio.sql y efectos/instrucciones en baja-logica-socios.md. Sin commit/push/merge.

Etapa vigente: API REST en feature/backend-api-rest, sin commit/push/merge. Los nueve endpoints del Anexo A están implementados; contrato y comandos en [api-rest.md](api-rest.md), colección en postman/. Verify actual: 95 aprobadas, 0 fallos/errores y 14 MySQL omitidas. Incluye 30 pruebas REST y 7 unitarias nuevas. MySQL nuevo, Postman y Axios pendientes; MVC sigue aprobando sus pruebas. Las etapas siguientes conservan el historial acumulado.

Etapa vigente: módulo MVC en feature/backend-mvc-thymeleaf, sin commit/push/merge. Ver [módulo de Secretaría](modulo-mvc.md): panel, ABM de actividades, alta/edición de socios, inscripción y bajas lógicas. Reportes disponibles al cierre: 71 aprobadas, 0 fallos/errores/omisiones. Codex repitió las 58 pruebas sin MySQL, todas aprobadas. Confirmación final del goal verify y recorrido en navegador pendientes. La integración anterior RN1–RN5 a dev quedó en a6b83dc; los estados de publicación anteriores corresponden a esa etapa.

Índice acumulativo del TPI de Taller de Construcción de Software, Universidad Siglo 21. Actualizado el 2026-11-08. No sustituye las fuentes ni duplica el detalle técnico.

| Área | Estado y fuente |
| --- | --- |
| Consigna y trazabilidad | [Requerimientos](requerimientos.md) y PDF en Enunciado; RF integrales todavía dependen de servicios/controladores e integración |
| Arquitectura y estructura | [Arquitectura](arquitectura.md), [estructura](estructura.md) |
| Modelo, SQL y datos ficticios | [Modelo](datos-modelo.md), [datos](datos-ficticios.md), database/schema.sql y seed.sql |
| Persistencia JPA anterior | [Validación](validacion-modelo-datos.md): 13 pruebas y verify aprobados, arranque MySQL y HTTP 200 confirmados por el usuario; integrado mediante PR #3, merge f0633c0 |
| Entorno por computadora | [MySQL local](mysql-local.md), [instalación](instalacion-desde-cero.md), backend/.env.example y scripts/load-env.ps1. .env privado e ignorado |
| Etapa actual RN1–RN5 | [Reglas y pruebas](reglas-negocio.md): InscripcionService, 36 pruebas aprobadas (19 unitarias de servicio, 5 integridad, 1 MVC, 7 persistencia y 4 integración de servicio), 0 fallos/errores/omisiones; verify exitoso confirmado por el usuario y XML inspeccionados por Codex |
| Defensa y autoría | [Guía](guia-defensa-tecnica.md), [bitácora IA](bitacora-ia.md), [trabajo humano](registro-trabajo.md). AH-1/AH-2/AH-3 conservan su alcance de configuración/operación/diseño |
| Pruebas integrales y alojamiento | [Pruebas de sistema](pruebas-sistema.md), [alojamiento](alojamiento.md); no afirmar integración/despliegue completo |

La etapa está auditada en feature/backend-reglas-negocio y autorizada para commit/push y PR hacia dev, sin merge. No modifica main/dev, frontends ni bases. El próximo paso es revisar el PR antes del merge; controladores e integración requieren una nueva etapa.

## Flujo individual de integración directa — 2026-11-08

Decisión humana vigente: desarrollo restante individual, conservando feature/* y dev, sin PR obligatorio. Integrar solo tras validación y confirmaciones necesarias, con merge commit para trazabilidad; no force push, no sobrescribir cambios y detenerse ante conflictos. main permanece fuera de alcance.

Auditoría MVC: los reportes actuales acreditan 71 aprobadas, 0 fallos/errores/omisiones, incluidas las 13 MySQL. Este dato reemplaza el estado histórico de 58/13 del desarrollo inicial; no prueba por sí solo el resultado del goal verify completo. Confirmación final de verify solicitada antes de commit/merge, como instruyó el usuario. Revisión visual manual no confirmada. Seguridad: sin autenticación/autorización ni CSRF implementado; módulo solo local, no apto para exposición pública. Detalle de auditoría y alcance en modulo-mvc.md.

## Confirmación de verify e integración autorizada — 2026-11-08

El usuario confirmó BUILD SUCCESS de Maven verify después de la auditoría. Codex volvió a inspeccionar los siete XML: 71 aprobadas, 0 fallos, 0 errores y 0 omitidas. Esto cierra la espera de confirmación de verify de las secciones anteriores. La ejecución autenticada fue del usuario; no se atribuye a Codex ni implica una revisión visual del navegador no comunicada.

Se continúa con el flujo individual autorizado: commit y push en feature/backend-mvc-thymeleaf, sincronización de dev, merge commit sin forzar y validación pertinente del árbol integrado antes de publicar origin/dev. Los hashes y resultados reales se informarán al completar cada operación; no se anticipa éxito en este registro. main queda fuera de alcance y la feature se conserva. No hay PR obligatorio.

Sin nuevas ediciones manuales de código confirmadas; AH-1/AH-2/AH-3 mantienen su alcance académico. Continúan los límites de seguridad: no hay autenticación/autorización ni protección CSRF implementada; el módulo no está preparado para exposición pública. La comprobación visual completa sigue pendiente. No se agregan funcionalidades ni se alteran bases o frontends.

## Propuesta humana de baja de socios — 2026-11-08

El usuario confirmó manualmente que GET /api/actividades devuelve JSON y propuso incorporar baja lógica de socios al detectar que solo existía baja de inscripciones. Es una decisión funcional humana de mejora voluntaria: RF5 de la consigna exige alta/modificación/listado de socios y RF9 baja de inscripción; el Anexo A no exige baja del socio. Codex realizó el análisis, no una implementación aprobada de esta mejora.

Socio solo posee estadoCuota (AL_DIA/MOROSO). Para ACTIVO/INACTIVO se necesita una columna y enum separados. Se detiene la implementación dependiente por instrucción expresa del usuario; no se modifican entidades, esquema, seed, servicios, controladores o tests para la nueva baja. Diseño, SQL de migración conservadora, transacción, alternativas PUT/PATCH, impacto y pruebas propuestas en propuesta-baja-socios.md. No hay migración ejecutada ni endpoint nuevo disponible.

Los XML disponibles en esta auditoría acreditan 109 aprobadas, 0 fallos, 0 errores y 0 omitidas, incluidas las 14 MySQL. No son una ejecución autenticada nueva de Codex ni acreditan la funcionalidad propuesta. El verify anterior de Codex fue 95 aprobadas/14 omitidas. No se infiere el éxito del goal verify actual solo de los XML. Postman conserva nueve endpoints y 18 requests, sin runner confirmado. Se mantiene ausencia de autenticación/CSRF y uso local. AH-1/AH-2/AH-3 siguen con sus límites; propuesta funcional no se registra como edición manual de código.

Pendiente de aprobación: EstadoSocio y migración aditiva en socioclub/socioclub_test; baja atómica de ACTIVAS con INACTIVO; PUT idempotente y confirmación POST en MVC; rechazo 409 con código SOCIO_INACTIVO independiente de RN1–RN5. No se hace commit, push o merge ni se considera esta mejora lista para integrar.

## Baja lógica de socios aprobada y preparada — 2026-11-08

El usuario aprobó la mejora voluntaria que propuso al revisar la API: EstadoSocio ACTIVO/INACTIVO separado de cuota, baja automática atómica de ACTIVAS, rechazo de nuevas altas, PUT idempotente y confirmación MVC. Autorizó código, pruebas y scripts, pero NO ejecutar migraciones en socioclub ni socioclub_test. Codex implementó; no se atribuye al usuario programación manual y AH-1/AH-2/AH-3 conservan sus límites.

Implementación actual: SocioService bloquea socio y actividades distintas por ID, cambia solo ACTIVAS a BAJA e INACTIVO en una transacción READ_COMMITTED; conserva fechas/vínculos/cuota e historial. Edición general no reactiva; selector de nuevas altas excluye INACTIVOS. API agrega PUT /api/socios/{id}/baja, estadoSocio y codigo SOCIO_INACTIVO independiente de RN1–RN5. MVC agrega confirmación GET sin mutación y POST/PRG. React no se modifica y deberá tratar el campo/código nuevo al integrarse.

Evidencia nueva: verify offline con MySQL explícitamente desactivado, BUILD SUCCESS y JAR, 128 casos: 111 aprobadas, 0 fallos, 0 errores y 17 omitidas. Las pruebas MySQL nuevas de historial/liberación, rollback real y concurrencia, junto con el seed ampliado, solo compilaron. No se ejecutó SQL, backup, migración ni backend JPA contra las bases actuales. Los 109 aprobados previos no acreditan el modelo con estadoSocio.

Script completo preparado: database/migrations/001_estado_socio.sql. schema.sql actualizado solo para instalaciones nuevas; seed omite el campo y conserva registros existentes. Las bases actuales necesitan migración autorizada antes de usar el código nuevo con ddl-auto=validate. No ejecutar MySQL/Postman ni arrancar JPA antes de ese paso. Postman tiene 22 requests y diez endpoints (nueve obligatorios + mejora), JSON válido sin runner ejecutado.

Diseño, efectos, guardas SQL, respaldo, repetición e instrucciones controladas en baja-logica-socios.md; propuesta anterior conservada como histórica. Pendiente revisar script y aprobar explícitamente su ejecución por catálogo, luego validar ensayo y desarrollo. Sin autenticación/autorización/CSRF: sigue siendo demostración local. No está listo para integrar hasta validar el esquema y la suite autenticada. Sin commit, push o merge.

## Plan operativo de migración de socios — 2026-11-08

Documento vigente: [plan-migracion-estado-socio.md](plan-migracion-estado-socio.md). Contiene auditoría MySQL 8.4, respaldo/recuperación fuera de Git, SQL anterior/posterior y comandos PowerShell. Script endurecido por Codex con catálogo esperado declarado por el operador y CHECK binario idéntico en schema.sql. Asersiones adicionales de vigencia preparadas, no ejecutadas. No hubo SQL ni migración. La aplicación nueva requiere migrar el catálogo de destino antes del arranque JPA.

Orden obligatorio: autorización y respaldo recuperable → migración exclusiva de testing → conservación/esquema → 17 pruebas MySQL y verify → autorización específica y respaldo vigente → migración de desarrollo → conservación y arranque. La baja funcional se ensaya solo con datos ficticios en testing. Evidencia disponible previa: 111 aprobadas/17 omitidas, no validación autenticada del esquema. No está listo para integrar todavía. Propuesta funcional humana e implementación Codex diferenciadas; AH-1/AH-2/AH-3 sin alterar. No se hizo commit, push, merge ni cambio de React.

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

## Etapa vigente: React + Axios — feature/react-integracion-api

Backend integrado y publicado en dev: commit d58094c, merge 3ace199 y registro 08d7c40; regresión posterior segura 111/0/0/0 sin MySQL, además del verify humano anterior 128/0/0/0. React parte de dev actualizado 08d7c40 y conserva componentes/rutas/CSS del equipo. Ingreso, catálogo/detalle, consulta/alta/baja de inscripciones conectados a API real, sin fallback local. Cliente configurable, errores por regla/código, sesión con solo DNI y RN6 con centavos. Tests Axios/RTL: 58 aprobadas, cero fallos/omisiones; build de producción aprobado (Compiled successfully). Sin ejecución HTTP integrada contra bases por Codex; recorrido navegador/CORS y escrituras autorizadas pendientes. Esta feature NO se fusiona a dev. Arquitectura/trazabilidad/comandos en react-integracion-api.md.

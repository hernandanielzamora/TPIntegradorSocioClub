# API REST — SocioClub — 2026-11-08

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Estado actual: nueve endpoints obligatorios más PUT /api/socios/{id}/baja como mejora voluntaria propuesta y aprobada por el usuario. EstadoSocio independiente de cuota, baja atómica de ACTIVAS, confirmación MVC y codigo=SOCIO_INACTIVO. Código preparado, pero migración de ambas bases expresamente NO autorizada todavía. No arrancar JPA ni ejecutar MySQL antes de migrar el catálogo pertinente. Verify sin MySQL: 111 aprobadas, 0 fallos/errores, 17 omitidas (128 casos). Detalle vigente en [baja-logica-socios.md](baja-logica-socios.md); los resultados anteriores de 109 casos son históricos.

Implementación en feature/backend-api-rest. Base a93d450 (merge MVC). Sin commit, push, merge, despliegue, cambios de esquema ni modificaciones de React/sitio-publico.

## Fuente y alcance

Se leyó el Anexo A de la consigna original, página 13 de Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf, y se contrastó con requerimientos.md. Exige exactamente los nueve endpoints siguientes. No exige CRUD de socios: la administración de socios continúa en Secretaría MVC. No se agregan endpoints por suposición. Los cupos y el estado de cuota están en las respuestas existentes, sin rutas adicionales.

| Verbo | Ruta | Request / consulta | Éxito | Errores esperados |
| --- | --- | --- | --- | --- |
| GET | /api/actividades | disciplina opcional, coincidencia exacta | 200 array completo | 400 si filtro inválido |
| GET | /api/actividades/{id} | ID positivo | 200 actividad | 400 / 404 |
| POST | /api/actividades | JSON actividad | 201 actividad y Location | 400 / 409 |
| PUT | /api/actividades/{id} | JSON actividad completo | 200 actividad | 400 / 404 / 409 |
| DELETE | /api/actividades/{id} | ID positivo | 204 sin cuerpo | 400 / 404 / 409 |
| GET | /api/socios/dni/{dni} | DNI de 7/8 dígitos sin puntos | 200 socio | 400 / 404 |
| GET | /api/socios/{id}/inscripciones | ID positivo | 200 array de ACTIVAS | 400 / 404 |
| POST | /api/inscripciones | socioId y actividadId | 201 inscripción | 400 / 404 / 409 |
| PUT | /api/inscripciones/{id}/baja | Sin cuerpo obligatorio | 200 inscripción BAJA | 400 / 404 |
| PUT (mejora voluntaria) | /api/socios/{id}/baja | Sin cuerpo obligatorio | 200 socio INACTIVO, idempotente | 400 / 404 |

Un fallo técnico de persistencia devuelve 503 sin revelar SQL. El contrato de errores descrito aplica a los handlers de esta API; no se promete ese envelope para rutas inexistentes o errores del contenedor antes de elegir controlador. Las respuestas de error del MVC permanecen HTML.

## Arquitectura

ApiController → ApiConsultaService → servicios compartidos/repositorios → Hibernate/JDBC → MySQL.

El controlador no inyecta repositorios ni cuenta cupos. ApiConsultaService coordina las consultas y convierte entidades a DTO dentro de transacciones; para escribir delega en ActividadService o InscripcionService. La fachada también abarca las escrituras y su mapeo final en una transacción READ_COMMITTED, por lo que inscribir no hereda un aislamiento distinto. Los bloqueos y RN1–RN5 siguen en InscripcionService. No se reescriben reglas en REST.

Se añade una consulta de actividades por disciplina ordenada y una de inscripciones ACTIVAS por socio ordenadas por ID descendente. Los arrays no son páginas de Spring Data: el Anexo A pide todas las actividades y React trabaja con arrays. MVC mantiene sus páginas de diez filas. Los cupos se calculan con el conteo de ACTIVAS ya existente; son una fotografía informativa, no una reserva.

ActividadService.guardar ahora devuelve el ID guardado para poder devolver el recurso creado y su Location. Es el único ajuste al contrato del servicio administrativo; el controlador MVC ignora ese retorno y conserva su comportamiento. No se duplica el guardado en REST. La eliminación sigue protegiendo ACTIVAS y también BAJAS: preservar la FK/historial evita borrar referencias. No se modifica esquema ni se agrega cascada para conseguir un 204.

## Requests y responses

Los requests reutilizan los DTOs planos ActividadFormulario e InscripcionFormulario de SecretariaDtos, con las mismas restricciones Bean Validation que el MVC. No se enlazan entidades ni se aceptan IDs/relaciones arbitrarias desde una entidad JSON. Los responses nuevos se definen en ApiDtos. Reutilizar los DTOs de entrada evita divergencia de validaciones; no obliga al cliente a enviar nombres Java de clases ni un objeto formulario anidado.

Actividad (POST y PUT completo):

```json
{"nombre":"Yoga de ensayo","disciplina":"Bienestar","profesor":"Ana","dias":"Lunes","horario":"9:00","edadMinima":18,"edadMaxima":99,"cupoMaximo":15,"cuotaMensual":100.25}
```

ActividadResponse tiene esos mismos campos más id y cupoDisponible. Los importes son números JSON BigDecimal en el servidor, no strings con símbolo monetario.

SocioResponse: id, dni, nombre, apellido, fechaNacimiento (ISO YYYY-MM-DD), estadoCuota (AL_DIA/MOROSO), estadoSocio (ACTIVO/INACTIVO). Se omiten email, teléfono y colecciones inversas; no se exponen datos de contacto innecesarios para el ingreso de la app. No es autenticación: conocer un DNI no acredita identidad.

Inscripción (POST):

```json
{"socioId":1,"actividadId":8}
```

InscripcionResponse: id, socioId, actividadId, fechaInscripcion (ISO), estado y actividad (ActividadResponse anidada). La actividad no contiene inscripciones ni socio, cortando los ciclos JPA por diseño. Un socio existente sin ACTIVAS recibe []; inexistente recibe 404. La baja es idempotente, permite MOROSO y conserva la fecha y el vínculo.

No se inventó una ruta GET /api/inscripciones/{id}: el alta devuelve 201 con el recurso, sin Location a una ruta inexistente. POST de actividad sí devuelve Location al detalle implementado.

## Errores y validación

Errores consistentes: error (mensaje público), regla (RN1–RN5 o null), detalles (array campo/mensaje, sin valores rechazados) y codigo (SOCIO_INACTIVO para la mejora, null para errores previos). Ejemplo ilustrativo del Anexo A ampliado sin perder sus campos:

```json
{"error":"La actividad no tiene cupo disponible","regla":"RN1","detalles":[]}
```

ReglaNegocioException se traduce a 409: SIN_CUPO→RN1, INSCRIPCION_DUPLICADA→RN2, EDAD_FUERA_DE_RANGO→RN3, SOCIO_MOROSO→RN4, MAXIMO_ACTIVIDADES→RN5. No se deduce la regla comparando mensajes. RecursoNoEncontradoException→404. ValidacionSecretariaException o conflictos de integridad→409. Cuerpos inválidos, JSON ilegible, IDs negativos/no numéricos o DNI mal formado→400. Bean Validation devuelve detalles comprensibles; nunca se envían stack traces o mensajes SQL del driver.

ErroresApiController utiliza @RestControllerAdvice limitado a ApiController; ErroresSecretariaController conserva su ámbito MVC. No se añadió un catch genérico que convierta cualquier excepción de programación en un rechazo de negocio.

## Integración futura con React y seguridad

Se revisaron api.js, datosLocales.js y los componentes/páginas de app-socios. Axios ya apunta a REACT_APP_API_URL o http://localhost:8080/api. El contrato mantiene cupoDisponible, disciplina, profesor, dias, horario, edades, cuotaMensual, estadoCuota e inscripción con actividad anidada. La próxima etapa reemplazará los imports simulados y tratará error.response.data.error/regla y los códigos HTTP. No se cambió ningún archivo React ni se afirma integración de navegador ejecutada.

Flujo futuro: componente React → cliente Axios → /api → controller con @Valid → fachada de DTO → servicio compartido transaccional/bloqueos → repositorio → MySQL → DTO JSON → actualización de estado React. Rechazos 409 deben mostrarse sin fingir éxito ni cambiar cupos localmente antes de confirmar el servidor.

WebConfig existente restringe CORS a /api/** y CORS_ALLOWED_ORIGINS (predeterminados http://localhost:3000 y http://localhost:5500), sin wildcard. Métodos GET/POST/PUT/DELETE/OPTIONS y headers Content-Type/Accept, sin habilitar credenciales. No se ampliaron orígenes; se probaron preflights permitido y rechazado. Una terminal con otros valores de entorno debe configurarse de forma deliberada; no usar * para ocultar errores CORS.

No hay autenticación/autorización ni protección CSRF específica. No se desactivó una protección existente. CORS no impide llamadas desde PowerShell u otros clientes y no sustituye autenticación/CSRF. La aplicación continúa como demostración local: no exponer endpoints de datos personales o escritura públicamente sin medidas de seguridad adicionales.

## Pruebas reales

Maven verify offline ejecutado por Codex: BUILD SUCCESS, compilación y JAR generados. Se usó Maven 3.10.0 previamente instalado por el wrapper y su caché, sin leer .env ni activar MySQL. No se añadieron dependencias Maven.

| Suite | Casos | Aprobadas | Fallos | Errores | Omitidas |
| --- | ---: | ---: | ---: | ---: | ---: |
| ApiControllerTests (nuevas, MockMvc) | 30 | 30 | 0 | 0 | 0 |
| ApiConsultaServiceTests (nuevas) | 7 | 7 | 0 | 0 | 0 |
| SecretariaControllerTests | 23 | 23 | 0 | 0 | 0 |
| SecretariaServiceTests | 10 | 10 | 0 | 0 | 0 |
| InscripcionServiceTests | 19 | 19 | 0 | 0 | 0 |
| IntegridadModeloTests | 5 | 5 | 0 | 0 | 0 |
| SocioClubApplicationTests | 1 | 1 | 0 | 0 | 0 |
| PersistenciaMySqlTests | 7 | 0 | 0 | 0 | 7 |
| InscripcionServiceMySqlTests (una nueva) | 7 | 0 | 0 | 0 | 7 |
| Total | 109 | 95 | 0 | 0 | 14 |

MockMvc verifica los nueve endpoints, JSON, 201/Location, 204 vacío, RN1–RN5 con 409, 400/404/503 y preflight CORS. Los unitarios verifican mapeo, consultas de ACTIVAS, distinción entre socio inexistente y vacío y delegación a servicios compartidos. Las 24 pruebas MVC previas continúan aprobadas (23 Secretaría y 1 raíz), sin mezclar advice JSON/HTML.

La prueba MySQL nueva cubre creación de actividad, filtro, socio por DNI, alta con DTO/cupo, consulta de activas, baja/restauración de cupo y eliminación sin historial, a nivel servicios JPA. No se presenta como una prueba HTTP con MySQL. Reutiliza la suite con socioclub_test, credenciales obligatorias, tablas vacías y limpieza solo de IDs propios. Se compiló pero no se ejecutó autenticada en esta etapa. No se tocaron datos de socioclub. No confundir con las 71 aprobadas de la etapa MVC anterior.

## Postman y pruebas manuales

Colección importable postman/SocioClub.postman_collection.json y entorno opcional SocioClub-local.postman_environment.json. Contiene 18 requests, ejemplos y aserciones de estado/regla. JSON parseado correctamente; no se ejecutó Postman ni Newman. Instrucciones, variables, orden de ABM y escenarios RN en postman/README.md. Los ejemplos son ficticios y los IDs deben verificarse, no asumirse por el seed.

Desde backend:

```powershell
.\scripts\load-env.ps1
if ($?) {
    .\mvnw.cmd -B verify
}
# Después de comprobar el resultado:
.\mvnw.cmd spring-boot:run
```

Con la configuración MySQL se esperan 109 pruebas sin omisiones; ese resultado sigue pendiente. Para ejecutar solo las nuevas pruebas sin credenciales: .\mvnw.cmd -B '-Dtest=ApiControllerTests,ApiConsultaServiceTests' test.

Desde otra terminal, consultas de lectura:

```powershell
$apiUrl = 'http://localhost:8080/api'
Invoke-RestMethod "$apiUrl/actividades"
$disciplina = [uri]::EscapeDataString('Bienestar')
Invoke-RestMethod "$apiUrl/actividades?disciplina=$disciplina"
Invoke-RestMethod "$apiUrl/socios/dni/30111222"
# Reemplazar estos IDs por valores reales de prueba:
Invoke-RestMethod "$apiUrl/actividades/8"
Invoke-RestMethod "$apiUrl/socios/1/inscripciones"
```

Escrituras manuales únicamente con registros ficticios elegidos; modifican la base configurada del backend:

```powershell
$cuerpo = @{ socioId = 1; actividadId = 8 } | ConvertTo-Json
$inscripcion = Invoke-RestMethod -Method Post -Uri "$apiUrl/inscripciones" -ContentType 'application/json' -Body $cuerpo
Invoke-RestMethod -Method Put -Uri "$apiUrl/inscripciones/$($inscripcion.id)/baja"
```

IDs 1/8 son ilustrativos; no garantizan cupo/edad/estado. Ante 409 conservar el mensaje real y corregir el escenario, no las reglas. Importar la colección desde Postman / Import / Files, elegir entorno local y revisar variables antes de enviar escrituras. No ejecutar todos los escenarios sin preparar sus condiciones.

## Problemas, autoría y revisión humana

No se reprodujo un ciclo infinito ni un fallo de negocio en esta etapa: se evitó la serialización de entidades usando DTOs. La primera corrida de las 30 pruebas REST aprobó. La lectura del PDF requirió un lector temporal pypdf en .cache, sin añadir dependencia al backend; sus permisos iniciales impidieron leerlo en el sandbox y se ejecutó con autorización. Eso fue una limitación de herramientas, no un error del sistema ni un ajuste humano de código.

Se revisaron y corrigieron referencias de IDs inconsistentes en los ejemplos ilustrativos de la colección antes de cerrarla. Corrección automática de Codex; no implica que Postman haya sido ejecutado.

Oportunidades reales de revisión humana: contrato de datos personales de SocioResponse y forma de errores JSON frente al cliente Axios; alcance de eliminación con historial en ActividadService; pruebas del flujo UI al reemplazar los mocks. AH-1/AH-2/AH-3 conservan sus límites; no se registra una edición humana adicional que no ocurrió. El usuario solicitó la API; Codex implementó código, tests, colección y documentación.

Para la defensa: ¿por qué DTO? Evita ciclos y limita datos públicos. ¿Por qué nueve endpoints? Son los mínimos verificados en Anexo A. ¿Por qué 409? Hay conflicto con el estado del negocio y regla identificable. ¿Por qué conservar servicios? MVC y REST aplican las mismas reglas y bloqueos. ¿CORS autentica? No. ¿Qué falta acreditar? MySQL nuevo, llamadas reales/Postman y futura integración Axios; no están aprobados por una prueba MockMvc.

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

## Cierre API y ensayo humano confirmado — 2026-11-08

El usuario confirmó carga del fixture mediante SQL convencional con transacción y COMMIT manual, y ensayo de baja correcto desde Secretaría con backend de testing en 127.0.0.1:8081. Se preservan como antecedentes los errores 1064 (near '' y luego near '//' en línea 127) al intentar el procedimiento. No existe evidencia suficiente para atribuir causa raíz definitiva al delimitador. La alternativa convencional funcionó según ejecución humana; Codex sustituyó database/fixtures/ensayo-baja-socio.sql para reflejarla, sin procedimientos/DDL, con guardas y decisión explícita COMMIT/ROLLBACK. Codex no ejecutó SQL ni alteró bases.

Pruebas completas válidas: 128 aprobadas, cero fallos/errores/omisiones, verify BUILD SUCCESS confirmado, ejecutado ANTES del fixture manual. Ahora socioclub_test contiene registros: NO repetir las suites que exigen vacío ni limpiar automáticamente. El ensayo correcto de Secretaría no demuestra por sí solo HTTP 409/SOCIO_INACTIVO, idempotencia, conservación exacta de filas, cupos finales o Postman; siguen pendientes de evidencia específica. La confirmación humana funcional es suficiente para continuar el cierre autorizado con estas limitaciones declaradas; no se inventa validación adicional.

Se mantienen IH-MIG-01 y AH-1–AH-3: migraciones/respaldos manuales y decisiones de configuración no equivalen automáticamente a ediciones de fuente. La alternativa SQL fue propuesta/aplicada por el usuario; el archivo versionable fue reescrito por Codex, no consta que el usuario lo editara. Sin autenticación/autorización/CSRF ni restauración de backups demostrada. Contrato y separación de capas en api-rest.md; fixture e instrucciones vigentes en validacion-manual-baja-socios.md. No es un despliegue público. Git se integra solo tras auditoría, sin PR y sin tocar main; hashes reales se registrarán después de ejecutarse.

## Ampliaciones propuestas: FE-ADMIN-01 y RN8 — Prompt 21

El usuario propuso administración React con autenticación/rol ADMIN y prevención de conflictos horarios como EXTRAS, sin reemplazar RF1–RF10/RN1–RN7. Auditoría/diseño/SQL revisables en [propuesta-admin-rn8.md](propuesta-admin-rn8.md). Se comprobó que dias es texto libre, horario solo contiene inicio y no existe parser/duración; tampoco hay seguridad administrativa ni CSRF. No se consultaron datos vivos ni se asumieron horas de fin.

Codex preparó 002_cuenta_administrativa.sql y 003_actividad_horario.sql, sin ejecutarlas ni agregar código de aplicación o dependencias. La condición expresa del pedido obliga a detenerse porque la integración necesita alterar el esquema. Administración protegida y RN8 NO están implementadas todavía; la aplicación actual conserva sus limitaciones de seguridad. Pendiente aprobación humana del modelo, inventario real y horas fin, provisión privada de cuenta y pruebas de las funciones nuevas. Los éxitos anteriores de tests no acreditan estas EXTRAS. Propuesta humana; diseño/DDL/documentación Codex, sin ediciones manuales ficticias ni cambio de AH-1–AH-3/IH-MIG-01. Sin commit/push/merge ni modificación de bases.

## Cierre obligatorio — decisión de alcance del usuario

Se postergan FE-ADMIN-01/RN8, sin implementarlas ni ejecutar su SQL. Se conserva auditoría; archivos reubicados en database/propuestas y detalles en [mejoras-futuras.md](mejoras-futuras.md). La decisión de priorizar requisitos/entrega es humana; reubicación/documentación de Codex. Las aprobaciones de tests anteriores son antecedentes, no pruebas nuevas de este cierre. No se borra trabajo del equipo.

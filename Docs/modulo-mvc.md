# Módulo MVC de Secretaría — 2026-11-08
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Actualización vigente: baja lógica de socios preparada por aprobación del usuario; ejecución de migraciones NO autorizada en ninguna base. Verify actual sin MySQL: 111 aprobadas, 0 fallos/errores, 17 omitidas. Código nuevo requiere migrar el catálogo antes de usar JPA. Script completo en database/migrations/001_estado_socio.sql y efectos/instrucciones en baja-logica-socios.md. Sin commit/push/merge.

Etapa en feature/backend-mvc-thymeleaf, desde el merge a6b83dc de InscripcionService a dev. Implementación de Codex por pedido del usuario. Sin commit, push, merge, despliegue, modificaciones de esquema o cambios en app-socios/sitio-publico.

## Alcance contra la consigna

Se revisó Docs/requerimientos.md, equivalente al archivo de requisitos mencionado por el usuario, las entidades/enums reales, repositorios, InscripcionService, el controlador inicial y sus templates. RF4 exige ABM/listado de actividades; RF5 alta/edición/listado de socios con apellido y paginación; RF6 inscripción con RN1–RN5; RF7 consulta de activos y cupo; RF9 baja lógica. Se implementan esos recorridos para Secretaría, sin declarar cumplidas la API REST o la integración con React.

El panel muestra cantidades de socios, actividades e inscripciones ACTIVAS. Los listados tienen páginas de diez filas; socios filtra por parte del apellido ignorando mayúsculas y actividades por disciplina exacta. El historial de inscripciones filtra por actividad y estado; vacío significa todos. Desde cada actividad se accede a sus inscriptos ACTIVOS. No se agregan eliminación de socios, login, reportes, pagos ni otras operaciones obligatorias inventadas.

## Arquitectura y archivos

`SecretariaController` adapta HTTP, formularios y modelos. `ErroresSecretariaController` presenta errores esperados con respuestas 400/404/503 sin detalles SQL. `InicioController` conserva GET / y la vista inicial; /secretaria agrega el resumen real. No hay repositorios inyectados en controladores.

`SocioService` consulta, crea y edita socios; controla DNI duplicado. `ActividadService` consulta, crea/edita/elimina actividades y protege cupo e historial. `ConsultaSecretariaService` obtiene resumen y páginas de inscripciones, mapeando relaciones a datos planos dentro de transacciones de lectura. `InscripcionService.inscribir` conserva RN1–RN5 y ahora ofrece `darDeBaja` con baja lógica idempotente, incluso para MOROSO.

`SecretariaDtos` contiene records de formularios, vistas, opciones y resumen. Los formularios solo aceptan campos permitidos; el ID a editar proviene de la ruta, no de una entidad enlazada al formulario. Las vistas reciben datos planos, sin colecciones inversas ni proxies JPA. Esto funciona con open-in-view=false y evita ciclos y accesos lazy desde HTML.

Templates: inicio, socios, socio-formulario, actividades, actividad-formulario, inscripciones, inscripcion-formulario y error bajo templates/secretaria; fragmentos comunes para head, navegación, mensajes y errores; estilos propios en static/css/estilos.css. Diseño institucional simple, formularios con etiquetas, foco visible, salto a contenido, tablas con encabezados, mensajes accesibles y adaptación a pantallas pequeñas sin dependencias visuales externas.

## Rutas

| Método | Ruta | Función |
| --- | --- | --- |
| GET | / | Inicio existente, sin cambiar su contrato |
| GET | /secretaria y /secretaria/ | Panel con resumen |
| GET | /secretaria/socios?apellido=...&pagina=0 | Listado y búsqueda |
| GET | /secretaria/socios/nuevo | Formulario de alta |
| GET | /secretaria/socios/{id}/editar | Formulario de edición |
| POST | /secretaria/socios y /secretaria/socios/{id} | Alta y modificación |
| GET | /secretaria/actividades?disciplina=...&pagina=0 | Catálogo paginado y cupos |
| GET | /secretaria/actividades/nueva | Formulario de alta |
| GET | /secretaria/actividades/{id}/editar | Formulario de edición |
| POST | /secretaria/actividades y /secretaria/actividades/{id} | Alta y modificación |
| POST | /secretaria/actividades/{id}/eliminar | Elimina solo sin inscripciones |
| GET | /secretaria/inscripciones?actividadId=...&estado=ACTIVA&pagina=0 | Nómina o historial paginado |
| GET | /secretaria/inscripciones/nueva | Selección de socio y actividad |
| POST | /secretaria/inscripciones | Inscribe mediante el servicio existente |
| POST | /secretaria/inscripciones/{id}/baja | Baja lógica, sin borrar historial |

Las mutaciones son POST. Un éxito redirige al listado con flash (Post/Redirect/Get), evitando reenviar por refresco. Un error de formulario retorna la misma vista con BindingResult y vuelve a cargar opciones sin perder la selección. Un error de eliminación redirige con mensaje. No se duplican las RN en controladores ni en JavaScript.

## Validaciones y errores

Bean Validation comprueba DNI de 7/8 dígitos, campos obligatorios/longitudes, email, nacimiento anterior a hoy, enums, números positivos, rango de edades coherente, horario, importe y precisión decimal. Se aplica en MVC y en los métodos de guardado de servicios con @Validated. Los mensajes predeterminados y errores de conversión se presentan en español mediante ValidationMessages.properties y messages.properties. HTML incluye restricciones útiles, pero la validación del servidor sigue siendo obligatoria.

RN1–RN5 siguen en InscripcionService. ReglaNegocioException y referencias inexistentes durante un alta se muestran en el formulario; editar un recurso que ya no existe devuelve 404. Los filtros no convertibles devuelven 400. Conflictos de integridad en un guardado tienen mensaje genérico comprensible. Otros errores de acceso a datos devuelven 503 sin stack trace, SQL ni valores internos. Thymeleaf usa th:text/th:errors (contenido escapado), nunca inserta nombres o mensajes con th:utext.

Una actividad con ACTIVAS no se elimina. Tampoco se elimina una con solo BAJAS: la FK existente y el requisito de historial impiden hacerlo sin destruir referencias. No se modificó el esquema ni se agregó borrado en cascada. Si se necesita ocultarla conservando historial, requeriría una decisión posterior sobre inactivación; no se implementa un estado nuevo sin autorización. Al editar, cupoMaximo no puede quedar por debajo del número de activas.

## Transacciones y baja

Las altas existentes mantienen READ_COMMITTED y bloqueos socio→actividad. La baja encuentra el vínculo, bloquea socio→actividad y cambia solo estado a BAJA por dirty checking; no cambia fecha, socio ni actividad y no comprueba morosidad. Repetirla deja BAJA. El alta nueva posterior conserva ese registro.

Editar socio bloquea al socio, por lo que no corre a la vez con una inscripción de la misma persona. Editar/eliminar actividad bloquea la actividad antes de contar y guardar, coordinándose con la reserva de cupo. Los nuevos servicios no llaman a inscribir dentro de una transacción externa de aislamiento distinto. Las consultas de lectura mapean a DTO dentro de la transacción.

## Recorrido de una inscripción

1. GET del formulario obtiene opciones planas desde los servicios y Thymeleaf genera selects.
2. POST recibe solo socioId/actividadId, convierte tipos y aplica Bean Validation.
3. El controlador llama al bean Spring de InscripcionService, sin contar cupos por su cuenta.
4. El servicio abre transacción, bloquea padres y valida existencia, cuota, edad, duplicado, máximo y cupo.
5. JpaRepository guarda la ACTIVA; Hibernate/JDBC ejecutan SQL en la base configurada y la transacción confirma.
6. Éxito: redirección al listado con mensaje flash. Rechazo esperado: mismo formulario con mensaje y datos conservados.

## Pruebas y resultados reales (reportes actualizados al cierre)

| Suite | Casos | Aprobados según reportes | Fallos/errores | Omitidos |
| --- | ---: | ---: | ---: | ---: |
| SecretariaControllerTests, MockMvc con Thymeleaf | 23 | 23 | 0 | 0 |
| SecretariaServiceTests | 10 | 10 | 0 | 0 |
| InscripcionServiceTests | 19 | 19 | 0 | 0 |
| IntegridadModeloTests | 5 | 5 | 0 | 0 |
| SocioClubApplicationTests, GET / | 1 | 1 | 0 | 0 |
| PersistenciaMySqlTests | 7 | 7 | 0 | 0 |
| InscripcionServiceMySqlTests (4 anteriores + 2 nuevas) | 6 | 6 | 0 | 0 |
| Total | 71 | 71 | 0 | 0 |

Primera ejecución de Codex, histórica: Maven verify sin MySQL BUILD SUCCESS, 58 aprobadas/13 omitidas y JAR generado. Los reportes posteriores disponibles al cierre acreditan 71 aprobadas sin fallos/errores/omisiones; confirmar verify completo en la terminal del usuario antes del merge. Se usó offline Maven 3.10.0 instalado por el wrapper y su caché, con autorización para sus locks fuera del sandbox. No se agregaron dependencias. Las 36 aprobadas de la etapa anterior son un resultado histórico distinto; no sustituyen validar las consultas y bajas nuevas contra MySQL.

MockMvc prueba panel, búsqueda/paginación, datos personales, actividades/cupos, opciones de inscripción, alta y edición de socios/actividades, éxito PRG, cinco códigos RN, entradas inválidas, consulta BAJA, baja POST, 404/400 y ocultamiento de errores SQL. Los unitarios cubren DNI duplicado, copia de campos permitidos, edición bloqueada, cupo mínimo compatible, eliminación sin/con historial y baja idempotente de MOROSO.

Las dos pruebas MySQL nuevas comprueban baja/consulta persistida para MOROSO y protección de edición/eliminación de actividad. Los reportes más recientes muestran ambas aprobadas; no fueron ejecutadas con credenciales por las herramientas de Codex. Reutilizan la suite exclusiva socioclub_test con tablas vacías, commits entre conexiones y limpieza solo de fixtures propias. No se leyó .env ni se conectó a socioclub desde tests. Una interrupción puede dejar fixtures: inspeccionar, no vaciar automáticamente la base.

## Problemas encontrados y correcciones automáticas reales

- La generación inicial de templates mediante una tubería PowerShell/Python degradó acentos a ?. La prueba existente de GET / lo detectó. Codex restauró los textos con escritura UTF-8 directa; no se relajó la aserción del título. Se corrigió también un rótulo de cupo y se revisaron los textos restantes.
- Un test esperaba al mismo tiempo un error de conversión y otro de Bean Validation en un record no construible. Codex separó las dos solicitudes: ID negativo con el otro válido y texto no numérico con el otro válido. Ambos escenarios permanecen; ninguna solicitud inválida llega al servicio de inscripción.
- Se verificó que la eliminación física con historial no es compatible con la FK; se devuelve un mensaje operativo y se conserva el historial. No se cambió SQL para evitar el problema.

Estas correcciones fueron de Codex, no de un integrante. No se inventan problemas ni acciones humanas adicionales.

## Validación manual pendiente

Desde backend, en la misma terminal PowerShell:

```powershell
.\scripts\load-env.ps1
if ($?) {
    .\mvnw.cmd -B verify
}
# Tras comprobar el resultado:
.\mvnw.cmd spring-boot:run
```

Se espera una suite total de 71 sin omisiones con la configuración MySQL; no se afirma todavía ese resultado. Para solo las nuevas MVC/unitarias: .\mvnw.cmd -B '-Dtest=SecretariaControllerTests,SecretariaServiceTests,SocioClubApplicationTests' test.

Abrir http://localhost:8080/secretaria. Verificar navegación, resumen, búsqueda por apellido/páginas, alta/edición de un socio ficticio, catálogo y cupos. Inscribir un socio apto, refrescar el listado (no repetir alta), consultar ACTIVAS, dar de baja, comprobar historial BAJA y plaza liberada. Intentar duplicado/MOROSO/edad/cupo/máximo con datos controlados; comprobar mensajes y selección conservada. Intentar reducir cupo por debajo de activas o eliminar una actividad con historial: debe rechazarse. Probar ancho de móvil, foco de teclado y formularios vacíos. Estas acciones manuales modifican los datos seleccionados: usar registros ficticios deliberadamente; no se ejecutaron por Codex.

## Revisión humana y límites

Dos puntos concretos para revisar juntos en VS Code antes de cerrar:

1. InscripcionService.darDeBaja: orden de bloqueos, conservación de fecha/relaciones e idempotencia, con pruebas de MOROSO. Si se propone un cambio real, ejecutar SecretariaServiceTests y la suite MySQL correspondiente.
2. ActividadService.eliminar/guardar: política de conservación con BAJAS y rechazo de reducción de cupo, con mensajes del controlador. Contrastar con la consigna y la FK antes de modificar código o esquema; las pruebas ya cubren los caminos actuales.

No se registró una edición manual humana en esta etapa: el usuario pidió el módulo y Codex implementó Java/HTML/CSS/tests. AH-1/AH-2/AH-3 conservan su alcance; solo se añadirá un ajuste cuando alguien efectivamente lo realice con diff, motivo y verificación.

El panel carece de autenticación/autorización y protección CSRF específica; esta entrega es local/académica, no un sistema administrativo listo para exposición pública. No se agregaron dependencias de seguridad fuera de alcance. Los selects cargan el catálogo completo y los cupos se cuentan por fila de página: adecuado para estos datos, optimización futura si aumenta el volumen. Cupos mostrados son informativos; solo el alta transaccional reserva una plaza. No se verificó visualmente en navegador ni se recibió confirmación manual de ese recorrido. Los XML sí acreditan las pruebas MySQL nuevas. API REST, integración React, despliegue e informe final de la cátedra siguen fuera de esta etapa.

## Preguntas para la defensa

- ¿Por qué DTO y no enlazar entidades? Se aceptan solo campos permitidos y se evita exponer relaciones JPA.
- ¿Por qué un servicio nuevo para consultas? Mantiene la transacción durante el mapeo y deja al controlador sin persistencia.
- ¿Qué evita reenviar al refrescar? Post/Redirect/Get tras éxito y mensajes flash de una sola navegación.
- ¿Dónde están RN1–RN5? En el servicio previo, reutilizado sin copiarlas a HTML o controladores.
- ¿Por qué no bloquear bajas de MOROSO? RN4 restringe nuevas altas, no conservar la posibilidad de baja.
- ¿Por qué no eliminar una actividad con BAJAS? La FK y el historial se conservan; no hay eliminación en cascada.
- ¿MockMvc valida MySQL? No: comprueba HTTP, binding, renderizado y llamadas simuladas. Las suites MySQL deben ejecutarse separadamente con su configuración exclusiva.


## Flujo individual y auditoría previa a integración — 2026-11-08

El usuario decidió continuar individualmente: conserva feature/* y dev y autoriza integrar funcionalidades validadas mediante merge directo, preferentemente con merge commit, sin PR obligatorio. No se atribuye al usuario una modificación manual del código MVC. AH-1/AH-2/AH-3 mantienen su autoría y límites académicos.

Auditoría: rama feature/backend-mvc-thymeleaf, 32 archivos de alcance MVC/servicios/tests/documentación revisados antes de esta actualización, sin cambios de REST, app-socios, sitio-publico o esquema. El controlador delega el alta a InscripcionService; no repite RN1–RN5. No se detectan impedimentos técnicos nuevos para una integración de uso local. .env sigue ignorado y fuera del índice; no se abrió. Sin secretos o temporales detectados entre los cambios revisados. diff --check aprobado.

Seguridad real: no hay dependencia Spring Security, SecurityFilterChain, login, autorización, generador/validador de token CSRF o token _csrf en formularios. No hay protección CSRF implementada. POST, PRG y Bean Validation no sustituyen CSRF. WebConfig limita CORS a /api/**, lo cual no protege formularios de /secretaria. El módulo no está preparado para exposición pública sin autenticación/autorización y medidas CSRF adicionales. No se agregan esas funcionalidades en este cierre.

Evidencia disponible: los siete XML del 2026-11-08 entre 13:55:34 y 13:55:49 muestran 71 aprobadas (23 MVC, 10 administración, 19 inscripción, 5 integridad, 1 raíz, 7 persistencia y 6 integración), 0 fallos, errores u omisiones. Se guardó una síntesis de metadatos y hashes en .cache/mvc-cierre-evidencia-reportes.json antes de repetir pruebas sin MySQL. No se deduce BUILD SUCCESS de verify únicamente del XML: queda pendiente la confirmación final solicitada al usuario. No se realizó commit, push o merge antes de obtenerla.

Repetición de Codex durante el cierre: las cinco suites sin MySQL ejecutadas mediante selección explícita sumaron 58 aprobadas, 0 fallos, 0 errores y 0 omitidas; goal test BUILD SUCCESS. No es una nueva corrida de verify ni incluye las 13 MySQL. Se conserva la síntesis de los XML previos de 71 aprobadas para distinguir ambas evidencias. Log local ignorado .cache/mvc-cierre-test.log.

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

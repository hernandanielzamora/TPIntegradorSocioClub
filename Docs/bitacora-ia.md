# Bitácora de uso de IA

> Cierre actualizado: el usuario confirmó la carga manual mediante SQL convencional y el ensayo correcto desde Secretaría en testing:8081. Los procedimientos/delimitadores fallidos se conservan al final como historial, NO como instrucciones vigentes. HTTP 409/codigo, idempotencia, conservación exacta, cupos finales y Postman siguen pendientes de evidencia específica. No repetir MySQL/verify que exige testing vacío: ahora contiene el fixture.
> Actualización vigente — 2026-11-08: el usuario confirmó respaldos independientes mediante mysqldump, migración manual de socioclub_test, Maven verify con 128 aprobadas/0 fallos/0 errores/0 omitidas y migración posterior autorizada de socioclub mediante Workbench. Codex corroboró los diez XML, sin ejecutar Maven ni SQL. No hay evidencia de restauración de prueba. Auditoría detallada de datos de desarrollo, arranque/HTTP posteriores y recorrido manual de baja siguen pendientes. Procedimiento vigente: [validacion-manual-baja-socios.md](validacion-manual-baja-socios.md). No volver a ejecutar la migración para seguir ese procedimiento.

## Antecedentes previos a esta confirmación

Las referencias siguientes a migración no ejecutada/no autorizada y a 111 aprobadas/17 omitidas describen el estado anterior. Se conservan como historial, no como estado vigente. La confirmación actual y sus límites están registrados al final de este documento.

Formato basado en el Anexo C (páginas 13–14) del [enunciado](Enunciado/TPIntegradorSocioClubEnunciado.pdf), ampliado con fecha, integrante, herramienta, revisión humana, corrección real, motivo y evidencia.

Las referencias internas al Anexo D son inconsistentes con el anexo existente. Se conserva como referencia el formato del Anexo C.

Registrar únicamente interacciones y resultados reales. La entrega final requiere al menos 5 entradas, con al menos 3 errores o ajustes detectados por el grupo. Las correcciones realizadas automáticamente por una herramienta deben distinguirse de las revisiones humanas: no se contabilizan como detecciones del grupo sin una revisión real documentada.

## Registro de interacciones

| N.º | Fecha | Integrante | Parte | Herramienta | Prompt (resumen) | Resultado | Revisión humana | Corrección real | Motivo | Evidencia |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | 2026-11-02 | No informado | F | Codex | Leer primero el PDF; crear matrices RF/RN, bitácora Anexo C, registro manual y README; no superar Prompt 01 ni desplegar. | Extracción íntegra con pypdf y documentación inicial. Se solicitó el texto ausente de Prompt 01. | No consta revisión de los archivos por el grupo. | En la interacción posterior, la herramienta detectó signos de interrogación en lugar de acentos y reescribió la documentación en UTF-8. No fue una corrección humana. | Corregir pérdida de caracteres causada por la escritura mediante la tubería de PowerShell. | PDF fuente, archivos Docs/*.md y salidas de extracción/lectura de esta sesión. |
| 2 | 2026-11-02 | No informado | A–F, inicialización | Codex, Spring Initializr, Create React App | Ejecutar Prompt 01: inspección, monorepo, tres aplicaciones mínimas, documentación, configuración local y alojamiento futuro, validación sin negocio ni commits. | Estructura creada; sitio inicial, página MVC, app CRA, CORS y variables de entorno. Maven verify generó el jar y aprobó una prueba MVC sin MySQL. CRA se generó y el primer build compiló; resultado definitivo tras ajustar React en el informe de validación. | El usuario aportó Prompt 01; no consta aprobación humana del código generado. | La herramienta corrigió el parent de Initializr 4.1.2.RELEASE a 4.1.2 tras fallo real de resolución y consulta a Maven Central. Fijó React/React DOM 18.3.1 porque React 19 ignora PropTypes. Usó npm.cmd ante bloqueo de npm.ps1. Impidió el commit automático de CRA. | Conservar dependencias publicadas, validación PropTypes docente, política PowerShell existente y ausencia de commits no autorizados. | backend/pom.xml; app-socios/package.json y package-lock.json; Docs/validacion-inicial.md; informe Surefire local y salidas de herramientas en esta sesión. |
| 3 | 2026-11-02 | No informado | Revisión técnica previa a GitHub | Codex | Auditar estructura, archivos publicables, secretos, exclusiones, enlaces y configuración; ejecutar Maven verify, build React y controles Git sin publicar ni modificar fuentes. | 65 candidatos, sin repositorios anidados ni indicios de secretos; 22 enlaces locales correctos; Maven y React exitosos; npm audit: 75 vulnerabilidades, ninguna crítica. Rama master y política de finales de línea en raíz identificadas como ajustes menores. | Auditoría automatizada; no consta revisión manual del código por integrantes. | No se modificaron fuentes. Se distinguió CRLF válido de whitespace real al ampliar el control a archivos sin seguimiento. | Evitar afirmar que git diff --check había cubierto archivos nuevos y evitar falsos positivos por CRLF. | Informe de auditoría de esta sesión; salidas locales ignoradas .cache/audit-maven.log, .cache/audit-react.log y .cache/audit-dependencies.json. |
| 4 | 2026-11-02 | No informado | Preparación de publicación | Codex, a solicitud explícita del usuario | Aplicar ajustes menores, registrar la decisión humana y crear el commit chore: initialize SocioClub monorepo; publicar main en el remoto indicado únicamente si está vacío, sin force. | Consulta remota exitosa y sin referencias; ajustes de .gitattributes y .gitignore aplicados; registros, README e inventario actualizados. Commit y push pendientes al escribir esta entrada previa al commit. | El usuario autorizó los ajustes y la publicación; esto no acredita revisión manual del código. | Cambios solicitados por el usuario y ejecutados por Codex: normalización declarativa de finales de línea, exclusión de credenciales y actualización de afirmaciones que quedarían desactualizadas al publicar. No se corrigieron funcionalidades ni versiones. | Ejecutar la decisión humana preservando la trazabilidad y el historial remoto. | Solicitud explícita en esta sesión; .gitattributes, .gitignore, Docs/registro-trabajo.md, README.md, Docs/estructura.md; el resultado final de commit y push se confirma en Git y en el informe de la sesión. |

## Plantilla para nuevas entradas

Completar una fila por cada interacción real con una herramienta de IA. Si un dato no está disponible, indicarlo como "No informado" o "Pendiente de completar"; no inferirlo.

| N.º | Fecha | Integrante | Parte | Herramienta | Prompt (resumen) | Resultado | Revisión humana | Corrección real | Motivo | Evidencia |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Pendiente | AAAA-MM-DD | Pendiente de completar | Pendiente | Pendiente | Resumir qué se pidió realmente. | Describir qué devolvió la herramienta. | Indicar qué revisó efectivamente el integrante y qué observó. | Describir el cambio realmente realizado. Si no hubo corrección, indicarlo. | Explicar por qué se aceptó, modificó o descartó el resultado. | Indicar archivo, captura, salida de consola o registro verificable. |

## Criterios para completar la bitácora

- **Fecha e integrante:** registrar la fecha real de la interacción y quién participó. No atribuir una tarea a una persona si no está confirmado.
- **Herramienta:** indicar el producto utilizado, por ejemplo Codex, ChatGPT, Copilot o Gemini.
- **Prompt:** resumir el pedido real, sin reconstruir instrucciones que no se conservaron.
- **Resultado:** describir lo que la herramienta devolvió o ejecutó, sin afirmar que funciona si no se comprobó.
- **Revisión humana:** indicar qué revisó realmente el integrante, qué encontró y cómo lo verificó. Si no consta revisión, escribirlo expresamente.
- **Corrección real:** diferenciar entre cambios hechos automáticamente por la herramienta y cambios decididos o realizados por integrantes.
- **Motivo:** explicar la razón concreta de una corrección, ajuste o rechazo.
- **Evidencia:** identificar archivos, capturas, salidas de consola, pruebas o registros que permitan verificar lo escrito. No declarar que existe una captura si no fue tomada.

## Observaciones de las entradas existentes

**Cierre observado de la entrada 2:** `npm ci` y el build final con React 18.3.1 finalizaron correctamente; `npm ls` no reportó dependencias inválidas. Jest 27.5.1 informó que no había pruebas. Se verificaron las 17 filas pendientes, los destinos de enlaces locales y 12 respuestas HTTP 200 para páginas y recursos estáticos; no se probó su ejecución JavaScript en un navegador. El puerto local 3306 no respondió. Ver el detalle y sus límites en [validación inicial](validacion-inicial.md).

**Validación previa al commit de la entrada 4:** origin configurado con la URL autorizada y rama local renombrada a main; 66 archivos preparados, `git diff --cached --check` sin errores y estadísticas revisadas. Se verificaron en el índice la exclusión de generados y archivos sensibles, la inclusión de ambos `.env.example` y configuración compartida, y la ausencia de indicios de secretos. Codex conservó además el permiso ejecutable de `backend/mvnw` en Git para Linux, sin alterar su contenido. Las advertencias CRLF/LF durante `git add` corresponden a la normalización solicitada. El éxito del commit y del push no se anticipa en esta entrada: se verificará después de crear este snapshot en el historial Git y en la respuesta final de la sesión.

## Estado de la bitácora

Actualmente se documentan ocho entradas reales, incluidas preparación manual y cierre. La cantidad mínima requerida por el enunciado es cinco, y al menos tres deben contener errores o ajustes detectados por el grupo. Las correcciones automáticas de Codex no acreditan detección humana de errores.

No se inventan entradas para alcanzar ese mínimo. La revisión humana y las nuevas intervenciones quedan pendientes de documentar cuando ocurran.

Las compilaciones no prueban requisitos funcionales ni una conexión real a MySQL.

## Entrada 5 — Modelo de datos y repositorios — 2026-11-07

| Campo | Registro real |
| --- | --- |
| Integrante | Usuario solicitante de esta sesión; identidad no informada. Sin atribución a otros integrantes. |
| Herramienta | Codex. |
| Solicitud | Inspeccionar PDF y PR integrados; implementar entidades y repositorios JPA, SQL MySQL y datos compatibles; configurar entorno, preparar/ejecutar pruebas y documentar defensa. Sin RN1–RN5, API, cambios en frontends, commits ni push. |
| Resultado generado por IA | Tres entidades, dos enums, tres JpaRepository, integridad con Bean Validation, SQL no destructivo y seed de ocho actividades/21 socios; cinco pruebas de integridad y siete optativas MySQL; guía, documentación y README actualizados. |
| Correcciones automáticas reales | Se sustituyó el primer borrador de INSERT con NOT EXISTS por LEFT JOIN, tras contrastar la limitación MySQL sobre subconsulta de la tabla destino. Se corrigieron acentos dañados al transferir texto por PowerShell/Python. Se eliminaron imports innecesarios y se actualizaron referencias documentales obsoletas. Son ajustes de Codex, no hallazgos del grupo. |
| Diferencias detectadas por IA | Los cupos fijos de React no se derivan de sus cuatro vínculos. PS02 necesita Fútbol lleno y PS03 necesita cupo libre: requieren preparación independiente. Se documentó la diferencia del comentario de Nicolás y los contactos de prueba. No se modificaron React ni los casos del grupo. |
| Revisión humana | Pendiente: no consta revisión del código ni ejecución manual por integrantes. El usuario sí autorizó iniciar MySQL84; no se atribuye esa decisión como revisión técnica del modelo. |
| Pruebas ejecutadas por Codex | Maven verify: BUILD SUCCESS, 6 aprobadas (5 integridad + 1 MVC), 7 omitidas MySQL, 0 fallos/errores. Revisión estática del seed frente a React/PDF y git diff --check aprobadas. Sin cambios en frontends. |
| Límites | Windows rechazó abrir MySQL84 al ejecutar Start-Service; servicio detenido y sin credenciales disponibles. No hubo conexión, ejecución SQL, validación real JPA ni persistencia. No se ejecutaron nuevamente pruebas React. |
| Evidencia | Docs/validacion-modelo-datos.md; Docs/datos-modelo.md; database/schema.sql y seed.sql; fuentes y pruebas del backend; informes locales Surefire y .cache/backend-modelo-verify.log (ignorados). |

Esta entrada registra una interacción real; no se desdobla artificialmente para completar mínimos ni se atribuyen las correcciones automáticas al grupo. No se alteran las entradas históricas.

## Entrada 6 — Diagnóstico MySQL y calidad de persistencia — 2026-11-07

| Campo | Registro real |
| --- | --- |
| Integrante | Usuario solicitante, identidad no confirmada. No se atribuye trabajo a otros integrantes. |
| Herramienta | Codex. |
| Solicitud | Diagnosticar y validar persistencia MySQL, bases separadas, tests y arranque; completar lo independiente y documentar procedimiento si hacen falta permisos administrativos. Sin nuevas funcionalidades, versiones, commits, push ni merge. |
| Diagnóstico de IA | Inicialmente MySQL84 detenido/manual, cliente 8.4.13 localizado, configuración leída, puerto sin listener, proceso no elevado y descriptor sin permiso de inicio para usuarios interactivos. Win32 1077 indicaba ausencia de inicio desde el arranque. Posteriormente se observó Running, códigos 0 y TCP 3306 accesible; no se confirmó quién lo inició. |
| Límites de acceso | Directorio Data denegado; no se leyeron logs internos ni se cambiaron permisos. Credenciales ausentes en proceso y usuario Windows; el servidor ahora acepta TCP, pero no se autenticó ni se crearon bases o ejecutó SQL. |
| Correcciones automáticas | Se detectó que la URL de pruebas no activaba la suite sin flag; ahora la activa. Se agregaron credenciales específicas de ensayo, recarga JPA tras clear y comprobación de base activa. Se amplió la documentación de conexión, validate, scripts e aislamiento. |
| Pruebas ejecutadas | verify normal: 6 aprobadas, 7 omitidas, 0 fallos/errores. Dos controles negativos: cada uno 0 aprobadas, 7 errores y 0 omitidas; uno rechaza socioclub antes de conectar, otro detecta conexión rechazada en un puerto sin servidor. No son persistencia aprobada. |
| Intervención humana real | Se recibió la solicitud y se pidió inicio administrativo. El servicio luego apareció Running, pero no se recibió confirmación de ejecución manual; no se afirma quién lo inició ni que el usuario preparó cuentas o revisó código. Se solicitó configurar credenciales localmente. La autorización anterior permanece en la entrada 5. |
| Resultado y pendientes | Compilación, prueba MVC e integridad en memoria aprobadas. Persistencia, carga de bases, queries JPA, Hibernate validate y arranque completo pendientes. Rama no aprobada aún para dar por validada la persistencia en un PR. |
| Evidencia | Docs/validacion-modelo-datos.md, sección Segunda intervención; Docs/mysql-local.md; PersistenciaMySqlTests.java; logs locales ignorados .cache/persistencia-*.log e informes Surefire del verify final. |

Las correcciones son de Codex y no se contabilizan como errores detectados manualmente por el grupo. Se preservaron los archivos de React/sitio público y los cambios locales de la intervención anterior.

## Entrada 7 — Preparación manual y permisos mínimos

El usuario confirmó mediante mensaje MySQL configurado con Workbench, dos bases y cuentas separadas; inicialmente informó que aún no había ejecutado scripts. Solicitó revisar permisos y esperar confirmación para operaciones manuales. Codex cambió PersistenciaMySqlTests a spring.sql.init.mode=never, mantuvo ddl-auto=validate y agregó SELECT DATABASE() antes de cada prueba. Así la cuenta de ensayo puede conservar solo SELECT/INSERT/UPDATE/DELETE sobre tablas precreadas.

Codex entregó pasos de Workbench para schema/seed en desarrollo y solo schema en ensayo. No ejecutó JPA ni atribuyó esa preparación a personas hasta recibir confirmación. Se actualizaron README, mysql-local, guía y validación. El usuario confirmó la preparación manual en el posterior pedido de cierre; no comunicó conteos o resultados definitivos de la aplicación.

## Entrada 8 — Cierre y publicación autorizada — 2026-11-08

| Campo | Registro real |
| --- | --- |
| Solicitud | Auditar archivos/secretos, ejecutar validaciones disponibles, documentar instalación en otra computadora y hacer commit/push de feature/backend-modelo-datos; PR hacia dev si es posible. |
| Herramienta e integrante | Codex; usuario solicitante sin identidad atribuida a otros integrantes. |
| Resultado automático | Revisión del modelo/repositorios/tests/SQL y archivos locales, guía de instalación desde cero, documentación vigente y preparación de un único commit coherente. |
| Corrección automática real | Se detectaron USE socioclub guardados en los scripts reutilizables. Se retiraron y se agregó rechazo de USE en el seed del test, para evitar desviar ensayo hacia desarrollo. |
| Pruebas ejecutadas | Maven verify: 6 aprobadas, 7 omitidas, 0 fallos/errores; React build exitoso; Jest/RTL: 20 aprobadas, 0 fallidas, 0 omitidas. git diff --check sin errores. |
| Límite MySQL | Sin credenciales/configuración segura disponibles para el proceso; no se solicitó ni imprimió contraseña. Preparación local confirmada por el usuario; persistencia real/arranque pendientes. |
| Intervención humana | Confirmación de MySQL, bases, cuentas, tablas y preparación manual; autorización explícita de commit/push y PR sin merge. No se presume revisión del código o ejecución JPA. |
| Publicación | Se prepara el commit con mensaje feat(backend): implement JPA data model and MySQL persistence. Hash, push y limpieza se verifican después en Git y en el informe final; no se anticipa éxito. GitHub CLI no está disponible. |
| Evidencia | Docs/validacion-modelo-datos.md (cierre), Docs/instalacion-desde-cero.md, README y diff/índice Git; informes locales Surefire y logs de cierre ignorados. |

Esta entrada documenta un pedido real distinto de la preparación anterior. El análisis y las correcciones automáticas no se contabilizan como errores detectados por el grupo.

## Entrada 9 — Diagnóstico de autenticación — 2026-11-08

Intervención humana comunicada: el usuario configuró variables temporales y ejecutó verify en PowerShell; informó 13 pruebas, 0 fallos, 7 errores y 0 omitidas. No se atribuye al grupo una corrección de código.

Trabajo automático de Codex: lectura de Surefire (incluida salida XML) y configuración JPA; identificación de AuthException causada por SQLException 1045/28000 para socioclub_tests@localhost. Dialecto y umbral de contexto son consecuencias. Corrección preventiva: exigir credenciales MYSQL_TEST_DB_USERNAME/PASSWORD al activar la suite para impedir fallback a desarrollo. No resuelve por sí sola el rechazo de autenticación. Se documentó el procedimiento de repetición local sin compartir secretos. Sin cambios de SQL, datos, permisos, dialecto, dependencias, commits, push o merge. La validación autenticada del ajuste queda pendiente en la terminal del usuario.

Verificación automática del ajuste: git diff --check aprobado. El intento de test-compile offline no llegó a Maven: el wrapper falló en su bootstrap PowerShell con Cannot index into a null array / Cannot start maven from wrapper, incluso ejecutado desde backend. No es un resultado de compilación ni una nueva prueba del acceso MySQL; no se modificó el wrapper. Compilación y repetición autenticada pendientes en la terminal del usuario.

## Entrada 10 — Aserciones CHECK JDBC — 2026-11-08

Decisión e intervención humana comunicadas: autenticación solucionada, ejecución de los siete tests (6 aprobados, 1 fallo, 0 errores/omisiones) y pedido de corregir exclusivamente la aserción conservando montos y estados inválidos. No se atribuye al grupo el diagnóstico ni la corrección de código.

Diagnóstico automático de Codex: Surefire acredita SQLException 3819 / HY000 por ck_actividad_cuota al actualizar cuota_mensual a -1. El test esperaba DataIntegrityViolationException y recibió UncategorizedSQLException. El escenario de estado no alcanzó a ejecutarse. Cambio automático: helper en PersistenciaMySqlTests que exige DataAccessException, causa SQL, código 3819, HY000 y nombre de CHECK específico en ambos escenarios. Es una corrección de la aserción, sin debilitar MySQL ni cambiar scripts, tablas o dependencias. Repetición autenticada pendiente en la terminal del usuario; no se ejecutó con credenciales desde Codex. Sin commit, push ni merge.

## Entrada 11 — Cargador local .env — 2026-11-08

Intervención y decisión humanas comunicadas: Maven completo con BUILD SUCCESS y pedido de configuración local reutilizable sin secretos versionados ni cambios de bases. No se atribuye al usuario la implementación automática ni se inventan conteos adicionales o validación HTTP.

Codex actualizó backend/.env.example, creó backend/.env únicamente con marcadores tras confirmar su exclusión con git check-ignore, e implementó backend/scripts/load-env.ps1. El archivo local no existía antes; no se leyeron credenciales reales. .gitignore ya protegía .env y permitía .env.example. El cargador interpreta texto sin evaluación, separa por el primer =, conserva #, admite comillas externas, valida variables obligatorias y separación socioclub/socioclub_app frente a socioclub_test/socioclub_tests y solo modifica entorno de proceso. No imprime valores.

Verificación automática real en Windows PowerShell con copia ficticia bajo .cache: carga desde directorio distinto, caracteres especiales/comillas, rechazo por variable ausente y por root sin actualización parcial. Se revisaron application.properties y credenciales exclusivas/guardias/rollback de PersistenciaMySqlTests sin modificarlos en esta intervención. Documentación ampliada en mysql-local.md y validacion-modelo-datos.md. No se ejecutaron Maven autenticado, migraciones, consultas, commits, push ni merge en esta intervención.

## Cierre de incidentes y supervisión humana — 2026-11-08

Los seis registros siguientes documentan hechos observados en esta conversación y reportes locales; satisfacen un mínimo de cinco registros de IA verificables. No son seis ajustes humanos. Usuario solicitante sin identidad atribuida a otros integrantes. Base publicada: 5217d1c5e301bd129719d829255c149255e9a5d4. Los ajustes actuales todavía no tienen commit. Los reportes se sobrescriben al repetir Maven: los errores originales constan en los extractos revisados durante la sesión y las entradas 9–11, mientras que los XML actuales acreditan el resultado final.

### Incidente 1 — Autenticación de la cuenta de pruebas

1. Objetivo: ejecutar persistencia real en socioclub_test con socioclub_tests.
2. Error: MySQL 1045 / SQLSTATE 28000, Access denied for user 'socioclub_tests'@'localhost' (using password: YES).
3. Detección: el usuario comunicó el verify fallido; Codex encontró AuthException y SQLException en la salida capturada del XML Surefire (líneas 170–174 y 308 de aquella versión).
4. Propuesta de Codex: revisar la autenticación con prompt oculto y cuenta de ensayo; no agregar dialecto ni tocar tablas. El log por sí solo no probaba qué dato de autenticación estaba mal.
5. Intervención humana: el usuario confirmó después que la autenticación se solucionó y, al cierre, que corrigió contraseñas locales. No consta ejecución del comando de diagnóstico concreto sugerido ni modificación de cuentas/permisos.
6. Código de Codex: protección adicional en configurar de PersistenciaMySqlTests.java para exigir MYSQL_TEST_DB_USERNAME/PASSWORD y eliminar fallback a desarrollo. No fue la solución de la contraseña.
7. Verificación: la corrida siguiente alcanzó los siete tests (6 aprobados y 1 fallo de aserción); el XML final muestra los siete aprobados.
8. Aprendizaje: acceso al servidor y autenticación son comprobaciones distintas; una cuenta de ensayo debe tener configuración independiente. Evidencia: entrada 9, test y XML final; cambio actual sin commit.

### Incidente 2 — Siete errores al iniciar ApplicationContext

1. Objetivo: validar entidades, relaciones y consultas mediante siete pruebas JPA.
2. Error: 13 tests, 0 fallos, 7 errores, 0 omitidos; Unable to determine Dialect without JDBC metadata y ApplicationContext failure threshold (1) exceeded.
3. Detección: salida manual aportada y revisión por Codex de reportes TXT/XML, buscando la primera excepción y no el último resumen de Maven.
4. Propuesta de Codex: resolver primero el rechazo 1045 del incidente 1. Los otros contextos no se reintentaban; no eran siete consultas JPA defectuosas.
5. Intervención humana: el usuario ejecutó Maven en su terminal con variables temporales, aportó el resultado y luego repitió tras resolver la autenticación.
6. Código de Codex: ningún arreglo independiente de Hibernate; la protección de credenciales es la del incidente 1, sin contabilizarla dos veces.
7. Verificación: los XML finales muestran 13 pruebas aprobadas, 0 fallos/errores/omisiones; el usuario confirmó BUILD SUCCESS de verify.
8. Aprendizaje: distinguir causa original, errores derivados y pruebas que no llegaron a ejecutar su cuerpo. Evidencia: entradas 9–10 y reportes finales; sin commit nuevo.

### Incidente 3 — Traducción de una violación CHECK

1. Objetivo: comprobar rechazo de cuota negativa y estado de cuota inválido desde SQL.
2. Error: se esperaba DataIntegrityViolationException y se recibió UncategorizedSQLException en rechazaEstadosYMontosInvalidosDesdeSql, antigua línea 179. UPDATE actividad SET cuota_mensual = -1 fue rechazado por ck_actividad_cuota, código 3819 / HY000.
3. Detección: el usuario aportó 7 tests, 1 fallo y 0 errores/omisiones; Codex leyó la causa SQLException del TXT Surefire y contrastó el nombre del CHECK con schema.sql sin modificarlo.
4. Propuesta de Codex: comprobar el error SQL específico en vez de exigir una categoría traducida por Spring. La base estaba aplicando correctamente la restricción.
5. Intervención humana: el usuario autorizó ese alcance mínimo, exigió conservar ambos escenarios y ejecutó después la validación exitosa. No escribió el helper Java en la evidencia disponible.
6. Código de Codex: comprobarViolacionCheck exige DataAccessException con causa SQLException, código 3819, HY000 y nombre de CHECK; ambos escenarios permanecen (ck_actividad_cuota y ck_socio_estado).
7. Verificación: siete tests JPA aprobados en XML final, incluyendo el método con ambos escenarios. No se eliminó ninguna restricción SQL.
8. Aprendizaje: un rechazo correcto de MySQL puede traducirse a otra categoría Spring; verificar el contrato SQL concreto evita un falso negativo. Evidencia: entrada 10, test y XML final; cambio sin commit.

### Incidente 4 — Variables temporales de PowerShell

1. Objetivo: ejecutar Maven y Spring sin guardar secretos versionados ni variables globales.
2. Limitación encontrada: las variables temporales pertenecen al proceso de terminal y sus hijos; otra terminal o el proceso ya iniciado de Codex no las hereda. No se inventa un error de código ni un mensaje de excepción específico por este comportamiento.
3. Detección: revisión del procedimiento de ejecución y distinción explícita entre terminal del usuario y herramientas de Codex durante la sesión.
4. Propuesta de Codex: configurar mediante Read-Host -AsSecureString y ejecutar Maven en esa misma terminal; repetir la carga en terminales nuevas.
5. Intervención humana: el usuario confirmó configurar las variables y ejecutar las pruebas en la misma terminal. Luego propuso .env para reducir la repetición.
6. Código de Codex: inicialmente ninguno; el cargador posterior corresponde al incidente 5.
7. Verificación: ejecución autenticada de Maven por el usuario; no se afirma herencia de variables a las herramientas de Codex.
8. Aprendizaje: los cambios de entorno de un proceso no reconfiguran procesos ya abiertos ni su padre. Evidencia: instrucciones previas y entrada 11; no es un ajuste humano de código.

### Incidente 5 — Propuesta humana de .env y ejecución automática

1. Objetivo: facilitar configuración local al abrir VS Code y trabajar entre computadoras.
2. Problema: había que volver a cargar las variables por terminal; Spring no lee .env automáticamente. Fue una mejora propuesta, no una excepción del backend.
3. Detección: el usuario planteó expresamente el problema y los requisitos de seguridad, parsing y portabilidad.
4. Propuesta técnica de Codex: plantilla versionada, archivo local ignorado y cargador PowerShell que interpreta texto sin evaluación, admite # y = y valida antes de aplicar.
5. Intervención humana: propuesta de la mejora, definición de requisitos, carga/corrección local de credenciales y validación posterior. No se atribuye al usuario la programación del script.
6. Código de Codex: backend/.env.example y backend/scripts/load-env.ps1; creación inicial de backend/.env solo con marcadores. .gitignore ya era suficiente. El script usa entorno Process, exige cuentas/bases separadas y no muestra valores.
7. Verificación: pruebas ficticias en .cache de caracteres, comillas, otra ruta, variables faltantes y rechazo de root sin carga parcial; git check-ignore confirma exclusión y git ls-files ausencia del índice. El log manual muestra Entorno local cargado en esta sesion. No se leyó .env durante la auditoría.
8. Aprendizaje: diferenciar archivo de configuración local, plantilla pública y entorno efectivo; cargar de nuevo tras editar .env. Evidencia: entrada 11, plantilla/script y documentación; sin commit nuevo.

### Incidente 6 — Cuenta de desarrollo y arranque completo

1. Objetivo: iniciar la aplicación normal contra socioclub con socioclub_app después de aprobar verify.
2. Error: 1045 / SQLSTATE 28000, Access denied for user 'socioclub_app'@'localhost' (using password: YES), seguido por fallo de metadatos/dialecto y salida 1 de spring-boot:run.
3. Detección: el usuario adjuntó el log completo del arranque de las 11:35 del 2026-11-08; Codex identificó la primera SQLException. El cargador había informado éxito antes del arranque.
4. Propuesta de Codex: contrastar autenticación directa de socioclub_app mediante prompt oculto, corregir DB_PASSWORD local, recargar y arrancar; no modificar esquema ni dialecto.
5. Intervención humana: el usuario confirmó corregir las contraseñas locales y reportó conexión JDBC exitosa a socioclub, MySQL 8.4.13, EntityManagerFactory, Tomcat 8080 y Started SocioClubApplication. No consta qué herramienta concreta usó para corregirlas.
6. Código de Codex: ninguno para resolver este rechazo; diagnóstico e instrucciones, luego documentación del cierre.
7. Verificación: confirmación manual del arranque exitoso, diferenciada de los XML de pruebas. HTTP GET / todavía no aportado.
8. Aprendizaje: aprobar las pruebas de ensayo no valida la contraseña de desarrollo; cuentas y bases deben verificarse por separado. Evidencia: log adjunto y confirmación final; sin commit nuevo.

### Clasificación inicial para la rúbrica y pendientes (actualizada por la confirmación ampliada posterior)

La rúbrica completa no fue aportada; esta clasificación es descriptiva y no garantiza puntaje. Posibles ajustes humanos de configuración: corrección de la contraseña de ensayo y corrección de la contraseña de desarrollo, comunicadas por el usuario. Son dos aspectos de una configuración local, y la cátedra podría considerarlos un único ajuste; no hay evidencia para contarlos como tres. La propuesta de .env es una decisión de diseño y mejora humana; la ejecución de Maven y la confirmación del arranque son validaciones humanas. El helper Java, la eliminación del fallback y el script PowerShell son implementaciones de Codex, no programación manual de un integrante.

Pendiente académico explícito: no hay evidencia de tres ajustes humanos distintos conforme a una rúbrica que exija correcciones manuales. Solicitar/registrar intervenciones futuras solamente cuando ocurran, con qué se modificó, quién intervino y cómo se verificó; no producir tareas artificiales ni reatribuir las correcciones automáticas. No se afirma que el grupo revisó manualmente líneas de código ni que ejecutó comandos concretos no confirmados.

## Trazabilidad de tres ajustes humanos — confirmación ampliada del 2026-11-08

Esta actualización incorpora información humana nueva y precisa el cierre anterior sin borrar los incidentes. El usuario confirmó expresamente que revisó socioclub_tests en Workbench, restableció su contraseña y comprobó la conexión con el cliente MySQL. Antes solo constaba una confirmación general de corrección de credenciales. Se registran tres ajustes humanos diferenciados de configuración, operación y diseño; no tres modificaciones manuales de código fuente. La aceptación por la cátedra depende del texto de la rúbrica, que no fue aportado.

| ID y naturaleza | Problema y evidencia inicial | Intervención humana efectivamente comunicada | Participación de Codex y archivos vinculados | Verificación y evidencia disponible | Relación con el requisito académico |
| --- | --- | --- | --- | --- | --- |
| AH-1 — Configuración de autenticación de testing | MySQL 1045/28000 para socioclub_tests@localhost; siete errores de contexto, entradas 9 e incidente 1 | El usuario revisó la cuenta en MySQL Workbench, restableció su contraseña y comprobó manualmente la conexión con el cliente MySQL | Codex diagnosticó el reporte y eliminó el fallback de credenciales en PersistenciaMySqlTests.java. Esa protección adicional no restablece la contraseña; no se atribuye su código al usuario | Conexión por cliente confirmada por el usuario. Ejecución comunicada: .\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests' test. Primero hubo un fallo CHECK independiente; tras corregir la aserción, el XML final registra 7 aprobadas, 0 fallos/errores/omisiones | Ajuste humano de configuración, con corrección y validación operativa. No demuestra edición manual de código generado por IA |
| AH-2 — Corrección operativa de credenciales de desarrollo | Log de arranque: 1045/28000 para socioclub_app@localhost, seguido de fallo de metadatos; incidente 6 | El usuario revisó y corrigió las credenciales locales de socioclub_app y comprobó el arranque del backend | Codex interpretó el log y dio instrucciones; no modificó Java ni cuentas para resolverlo. Archivos relacionados: backend/.env (privado), application.properties y load-env.ps1 | El log aportado muestra carga con .\scripts\load-env.ps1 y el intento con .\mvnw.cmd spring-boot:run. Tras corregir claves, el usuario confirmó JDBC a socioclub, MySQL 8.4.13, EntityManagerFactory, Tomcat 8080 y Started SocioClubApplication. No hay confirmación HTTP GET / | Ajuste humano operativo distinto por cuenta, base y objetivo. No demuestra corrección manual del código fuente; una rúbrica podría agruparlo con AH-1 |
| AH-3 — Decisión de diseño de gestión del entorno | Variables de sesión desaparecen al cerrar PowerShell; dificultad para reutilizar configuración entre equipos, incidente 5 y entrada 11 | El usuario propuso .env local, .env.example y un script de carga, y fijó requisitos de seguridad, separación de bases y portabilidad | Implementación de Codex: backend/.env.example, backend/scripts/load-env.ps1 y creación inicial de .env con marcadores. .gitignore ya era suficiente. El usuario completó las claves locales; no se atribuye programación del script al equipo | Archivos presentes y revisados; pruebas ficticias automáticas ya realizadas: #, =, comillas, otra ruta, falta de variables y rechazo de root sin carga parcial. git check-ignore y ausencia en índice comprobados. Log del usuario acredita carga exitosa. No se ensayó un ciclo completo en otra computadora | Ajuste humano de diseño con implementación asistida. No constituye edición manual del código generado por IA |

### Alcance de los comandos y la evidencia

El usuario confirmó el uso de Workbench y del cliente MySQL para AH-1, pero no aportó la sentencia de restablecimiento ni la línea exacta invocada en el cliente. No se reconstruyen como comandos ejecutados. La sugerencia previa de Codex fue mysql.exe --protocol=TCP --host=localhost --port=3306 --user=socioclub_tests --password --database=socioclub_test; se mantiene como procedimiento reproducible, no como transcripción de la acción humana. No se registra ninguna contraseña ni SQL con secretos.

La ejecución de .\mvnw.cmd -B verify con BUILD SUCCESS fue confirmada por el usuario y los XML inspeccionados acreditan 13 aprobadas (5 integridad, 7 persistencia, 1 MVC), 0 fallos, errores u omisiones. Los XML son evidencia del resultado final, no del restablecimiento de contraseña. Las comprobaciones del script fueron automáticas de Codex con datos ficticios, no pruebas manuales del equipo. En esta actualización solo se verificó nuevamente la existencia de la plantilla/script y se inspeccionaron sus mecanismos Process y parsing; no se leyó .env ni se conectó a MySQL.

Trazabilidad Git: base 5217d1c5e301bd129719d829255c149255e9a5d4. Los cambios actuales no tienen commit; las contraseñas y su restablecimiento no deben versionarse. Los reportes Surefire son locales e ignorados y se sobrescriben en cada ejecución; las confirmaciones humanas están documentadas aquí. No se inventan capturas ni logs adicionales.

### Contabilización y límite académico

Se contabilizan AH-1, AH-2 y AH-3 como tres ajustes humanos diferenciados en sentido amplio: configuración de testing, corrección operativa de desarrollo y decisión de diseño del entorno. Esta clasificación actualiza la conclusión anterior que no los contabilizaba expresamente. No acredita tres correcciones manuales del código fuente. Si la consigna exige específicamente modificar a mano código generado por IA, ese requisito sigue sin evidencia suficiente. Si exige ajustes humanos generales, estos casos aportan evidencia defendible, pero no se garantiza la aceptación: AH-1/AH-2 podrían considerarse una misma clase de corrección y AH-3 una propuesta en vez de una modificación manual.

Oportunidades reales para etapas futuras, solo cuando se implementen y se detecte la necesidad: un integrante puede revisar una regla de negocio contra el enunciado, corregir personalmente una condición defectuosa del código asistido y aportar una prueba antes/después; revisar una consulta/paginación y corregirla manualmente ante un caso reproducible; o corregir personalmente el manejo transaccional/validación de entrada si una prueba evidencia un defecto. Registrar autor, fragmento antes/después, motivo, prueba y commit. Son oportunidades pendientes, no tareas realizadas, y no se implementan RN1–RN5 aquí ni se introducen errores para cumplir un número.

## Entrada 12 — Auditoría y publicación autorizada — 2026-11-08

El usuario confirmó HTTP GET / 200 y autorizó auditoría, commit/push de feature/backend-modelo-datos y preparación/creación de PR a dev sin merge. Codex revisó Surefire: 13 aprobadas, 0 fallos, 0 errores, 0 omitidas. El verify, arranque y HTTP fueron ejecutados/confirmados por el usuario; Codex inspeccionó los reportes, sin leer .env.

Incidente adicional: plantilla con CLAVE_DESARROLLO/CLAVE_PRUEBAS mientras el cargador rechaza COMPLETAR_CLAVE_DESARROLLO/COMPLETAR_CLAVE_PRUEBAS. Codex detectó la discrepancia y detuvo la publicación. El usuario corrigió manualmente la plantilla y confirmó continuar; Codex verificó su alineación. No se atribuye esta corrección manual a Codex ni se garantiza que compute como modificación manual de código fuente según la rúbrica. No se alteran AH-1/AH-2/AH-3 ni el historial.

Se revisan cambios de tests, plantilla, cargador y documentación, exclusión de .env, archivos no rastreados y patrones de secretos, sin imprimir claves. git diff --check aprobado antes de preparar staging. Se actualizó origin mediante fetch sin modificar main/dev. Commit, push y revisión de conflictos se informan con su resultado real al finalizar; no se anticipan. No se modifica MySQL ni se implementan nuevas funcionalidades.

## Entrada 13 — Servicio de inscripción RN1–RN5 — 2026-11-08

Solicitud humana: implementar InscripcionService, reglas, pruebas y documentación sin controladores, cambios de frontends, esquema, commits o publicación. Rama inicial limpia feature/backend-reglas-negocio, commit f0633c0, merge del PR #3 de persistencia. Codex revisó entidades, enums, repositorios y documentación. El usuario confirmó por consulta explícita el rango completo de edad y continuar con mensajes claros de la consigna; son decisiones humanas, no correcciones manuales de código.

Implementación automática: InscripcionService con Clock, errores por código, búsqueda con PESSIMISTIC_WRITE de socio y actividad (siempre en ese orden) y transacción READ_COMMITTED; reutilización de consultas de ACTIVAS y alta nueva sin reactivar BAJA. Configuración FechaConfig y excepciones. Sin cambios de esquema ni dependencias. Se agregaron 19 casos unitarios y 4 MySQL para BAJA/historial y carreras por cupo/duplicado/tercer lugar, reutilizando la configuración exclusiva de ensayo de PersistenciaMySqlTests.

Decisión de prueba relevante: las carreras necesitan fixtures confirmadas entre conexiones. Las nuevas pruebas exigen socioclub_test vacía y verifican catálogo antes de crear/limpiar; borran solo sus IDs al finalizar, sin DDL/seed ni limpieza de datos previos. Una interrupción puede dejar fixtures, lo que se documenta para detenerse e inspeccionar. No se ejecutaron con credenciales ni se leyó .env.

Problema real de entorno: el primer Maven offline no pudo abrir archivos .locks en la caché del usuario desde el sandbox. Codex repitió con aprobación fuera del sandbox usando la distribución Maven 3.10.0 ya instalada por el wrapper. No se cambió wrapper ni se atribuye al usuario una corrección de código. Mockito emitió advertencia de agente dinámico Java 25, sin fallo. No se fabricaron defectos de negocio.

Resultados reales: prueba del servicio 19 aprobadas, 0 fallos/errores/omisiones. Verify offline completo BUILD SUCCESS, 36 casos: 25 aprobados, 0 fallos, 0 errores y 11 omitidos (7 de persistencia existentes y 4 nuevos MySQL). Compilación y JAR aprobados; concurrencia real y verify autenticado siguen pendientes. Los 13 aprobados de la etapa anterior son evidencia histórica y no se presentan como resultado nuevo. Reportes actuales en backend/target/surefire-reports.

Documentación: reglas-negocio.md, documento-maestro-proyecto.md, guía, requerimientos, validación y registro humano. Codex implementó el código; AH-1/AH-2/AH-3 conservan su autoría y límites. Se identifican oportunidades para revisión humana sin afirmar modificaciones que no ocurrieron. No hubo consultas/mutaciones de MySQL, commit, push, merge o despliegue.

## Entrada 14 — Auditoría y cierre de InscripcionService — 2026-11-08

Intervención humana real: el usuario confirmó aprobación de todas las pruebas MySQL y Maven verify y autorizó commit/push de feature/backend-reglas-negocio y PR hacia dev, sin merge. No comunicó nuevas modificaciones manuales de código; la validación y autorización no se presentan como programación humana.

Codex revisó servicio, consultas de bloqueo, Clock/Period, excepciones, suites unitarias/integración y guardias de catálogo. Los XML finales acreditan 36 aprobadas: 19 servicio, 5 integridad, 1 MVC, 7 persistencia y 4 integración de servicio; 0 fallos/errores/omisiones. Se actualizan los documentos vigentes y se conserva la primera ejecución 25/11 como antecedente histórico. No se ejecutó SQL ni se abrió .env en el cierre.

Resultado de revisión: sin impedimentos detectados para presentar el PR. Se documenta que todas las altas deben usar el bean Spring/protocolo de bloqueos y no heredar aislamiento incompatible; las pruebas cubren los escenarios implementados, no todos los intercalados posibles. Sin modificaciones nuevas de código en la auditoría. Se revisa exclusión de .env y se inspeccionan archivos publicables, sin imprimir secretos. No se alteran bases, esquema, dependencias o frontends.

Commit/push y PR se preparan con autorización explícita; sus resultados se comprueban al final, sin anticipar éxito ni hash. Mensaje previsto: feat(backend): implement transactional enrollment business rules. El usuario revisará el PR antes de cualquier merge.

## Entrada 15 — Secretaría MVC y Thymeleaf — 2026-11-08

Pedido humano: desarrollar el módulo administrativo en feature/backend-mvc-thymeleaf, reutilizando RN1–RN5, con DTOs, validaciones, pruebas, documentación y dos oportunidades de revisión manual; sin API, cambios de frontends/esquema, publicación o despliegue. Rama inicial limpia; modelo/servicio previo integrado a dev en a6b83dc. Codex revisó los requisitos equivalentes en requerimientos.md y los componentes reales.

Implementación de Codex: SecretariaController y advice de errores, DTOs record con Bean Validation, SocioService, ActividadService y ConsultaSecretariaService, baja lógica idempotente en InscripcionService, consultas paginadas para nómina/historial, ocho vistas y fragmentos/CSS, mensajes en español. GET / preservado, panel bajo /secretaria. Los controladores no inyectan repositorios ni repiten reglas. Los cambios usan POST y PRG en éxito. La administración conserva FK/historial y coordina cupos con bloqueo de actividad; baja mantiene socio→actividad y permite MOROSO.

Problemas reales y solución automática: los acentos de templates se degradaron al generar HTML mediante tubería PowerShell/Python; el test existente del título falló y Codex restauró UTF-8 sin relajar la aserción. Además, un test pedía simultáneamente error de conversión y validación de otro campo en un record no construible; Codex separó las dos solicitudes inválidas, conservando ambas verificaciones. Primer ensayo: 24 casos con 2 fallos; segundo ensayo: 34 casos con 1 fallo por un texto de título aún pendiente; después verify final aprobado. Se revisó el rótulo de cupo y se agregaron mensajes de conversión/Bean Validation en español. No se atribuyen estas correcciones a un integrante.

Resultado final ejecutado: Maven 3.10.0 offline con la distribución/caché del wrapper, sin cargar .env ni activar MySQL, con autorización para los locks de caché fuera del sandbox. Verify BUILD SUCCESS, JAR generado, 71 casos: 58 aprobados, 0 fallos, 0 errores y 13 omitidos. Desglose aprobado: 23 MVC nuevas, 10 unitarias administrativas, 19 de inscripción previas, 5 integridad y 1 MVC raíz. Omitidas: 7 persistencia y 6 integración de servicio (incluye 2 nuevas de baja/consulta y administración). No hay SQL autenticado ni verificación visual del navegador en esta etapa.

Documentación: modulo-mvc.md, maestro, guía, requerimientos y registros. Revisión humana futura justificada: baja transaccional e idempotente y política de edición/eliminación de actividad con historial. Ninguna modificación manual humana nueva fue comunicada; no se agrega un AH ficticio. Límites: API/React no integrados, sin autenticación/CSRF específica y sin despliegue. No se hicieron commits, push o merge ni se alteraron bases o schema/seed.

## Entrada 16 — Auditoría MVC y cambio de flujo individual — 2026-11-08

Decisión humana comunicada: continuar el desarrollo individual y conservar feature/*/dev sin PR obligatorio; integrar MVC directamente solo después de validar, con commit/push de feature y merge commit hacia dev, sin tocar main y deteniéndose ante conflictos. El usuario indicó expresamente esperar confirmación si verify requiere su terminal con MySQL. No se registra una edición manual humana de Java/HTML en este cierre.

Trabajo de Codex: inspección de rama, cambios, reutilización del servicio, reportes, .gitignore y configuración de seguridad. Los XML disponibles al comenzar acreditan 71 aprobadas, 0 fallos/errores/omisiones; los 13 MySQL están ejecutados. Se guardan metadatos/fechas/hashes en .cache/mvc-cierre-evidencia-reportes.json antes de repetir las 58 pruebas sin MySQL. No se abre .env ni se ejecuta SQL autenticado. El goal verify completo requiere confirmación final del usuario; no se infiere su éxito solo de los XML.

Estado de seguridad corroborado: sin Spring Security, login/autorización, SecurityFilterChain, token CSRF o validación equivalente en formularios. CORS se configura solo para /api/** y no equivale a CSRF. POST/PRG no protegen esas operaciones contra CSRF. Se documenta uso local y necesidad de medidas adicionales antes de exposición pública, sin añadir funcionalidades fuera de alcance.

Sin problemas técnicos nuevos detectados para uso local; diff --check y exclusión de .env aprobados, sin secretos o temporales detectados en archivos publicables. Documentación actualizada en maestro, módulo, guía y registros. No se hace commit/push/merge antes de la confirmación pendiente ni se inventan ajustes humanos; AH-1/AH-2/AH-3 siguen identificados.

Resultado posterior de la repetición propia de esta auditoría: goal test con selección explícita de las cinco suites sin MySQL, 58 aprobadas, 0 fallos, 0 errores, 0 omitidas, BUILD SUCCESS. No equivale a repetir verify completo; no sobrescribe la evidencia de las 13 MySQL disponibles. Log ignorado .cache/mvc-cierre-test.log. Se espera confirmación del usuario antes de publicar o integrar.

## Confirmación de verify e integración autorizada — 2026-11-08

El usuario confirmó BUILD SUCCESS de Maven verify después de la auditoría. Codex volvió a inspeccionar los siete XML: 71 aprobadas, 0 fallos, 0 errores y 0 omitidas. Esto cierra la espera de confirmación de verify de las secciones anteriores. La ejecución autenticada fue del usuario; no se atribuye a Codex ni implica una revisión visual del navegador no comunicada.

Se continúa con el flujo individual autorizado: commit y push en feature/backend-mvc-thymeleaf, sincronización de dev, merge commit sin forzar y validación pertinente del árbol integrado antes de publicar origin/dev. Los hashes y resultados reales se informarán al completar cada operación; no se anticipa éxito en este registro. main queda fuera de alcance y la feature se conserva. No hay PR obligatorio.

Sin nuevas ediciones manuales de código confirmadas; AH-1/AH-2/AH-3 mantienen su alcance académico. Continúan los límites de seguridad: no hay autenticación/autorización ni protección CSRF implementada; el módulo no está preparado para exposición pública. La comprobación visual completa sigue pendiente. No se agregan funcionalidades ni se alteran bases o frontends.

## Entrada 17 — API REST del Anexo A — 2026-11-08

Solicitud humana: API REST en feature/backend-api-rest, contrato fiel a la consigna, reutilización de servicios, DTOs, validaciones, errores JSON, colección Postman, pruebas y documentación. Sin cambios de React/sitio-publico/esquema, dependencias innecesarias, commits, push, merge o despliegue. Rama inicial limpia desde a93d450 (MVC integrado).

Codex revisó servicios, MVC, modelos, configuración CORS, requisitos y las interfaces/datos simulados de React. Leyó el PDF original, página 13: nueve endpoints exactos y formato error/regla para rechazos. El lector pypdf se descargó solo en .cache por autorización; no es dependencia Maven. Su lectura inicial desde sandbox no tuvo acceso a los archivos instalados, y se repitió con autorización. Limitación de herramienta, no fallo del proyecto.

Implementación automática: ApiController con nueve endpoints, ApiDtos de salida/error, requests que reutilizan DTOs validados de Secretaría, ApiConsultaService para mapear dentro de transacciones y delegar a servicios existentes, advice JSON acotado a API y consultas ordenadas de disciplina/ACTIVAS. ActividadService.guardar retorna el ID para respuestas 201/Location sin duplicar el alta; el MVC ignora el retorno y conserva su contrato. No se modificaron reglas ni seguridad para conseguir tests verdes. No se cambió CORS, se verificaron orígenes permitidos/rechazados.

Resultados ejecutados: primera suite REST 30 aprobadas, sin fallos/errores/omisiones. Verify offline final BUILD SUCCESS y JAR: 109 casos, 95 aprobadas, 0 fallos, 0 errores, 14 omitidas (7 persistencia y 7 integración de servicio, con una nueva de API a nivel JPA). Incluye 7 unitarias de fachada nuevas y regresión MVC 23+1 aprobada. No se leyó .env ni ejecutó MySQL autenticado; no se inventa un ciclo JSON o fallo de negocio no observado.

Postman: colección v2.1 y entorno sin secretos, 18 requests con nueve rutas, ejemplos, casos válidos/invalidos/RN y scripts. Codex comprobó parsing JSON y corrigió referencias inconsistentes de IDs en ejemplos antes del cierre; no ejecutó Postman/Newman. Los escenarios RN requieren datos/IDs deliberados. Documentación API, maestro, guía, requisitos, README Postman y registros actualizada. No se atribuye implementación o corrección automática al usuario; no hubo modificación manual humana comunicada y AH-1/AH-2/AH-3 conservan su alcance. Pendientes: verify autenticado, llamadas reales y futura integración Axios, con seguridad antes de exposición pública.

## Entrada 18 — Propuesta humana de baja lógica de socios y parada por esquema — 2026-11-08

El usuario comunicó prueba manual GET /api/actividades con JSON correcto y una propuesta propia: baja lógica de socios preservando historial. Contexto humano real: durante su revisión detectó que la gestión de inscripciones no contemplaba explícitamente baja del socio. Es una propuesta funcional, no una edición manual del código ni una regla obligatoria omitida.

Codex verificó el PDF original (páginas 3 y 13), rama feature/backend-api-rest, API/DTO/advice/CORS y schema/entidad. RF5 no exige baja del socio y RF9 trata inscripciones; solo existe estadoCuota. Se identificó necesidad de migración y se detuvo el trabajo dependiente conforme al pedido. No se agregaron EstadoSocio, endpoints, tests o scripts ejecutables de la mejora ni se alteraron bases. Se preparó propuesta-baja-socios.md con SQL aditivo comentado como no ejecutado, impacto en ambas bases/seed/JPA/MVC/API/React, protocolo socio→actividades ordenadas, rollback de negocio y advertencia sobre DDL con commit implícito. PUT idempotente y baja automática de ACTIVAS son recomendaciones de Codex pendientes de aprobación.

Evidencia de auditoría actual: los nueve XML muestran 109 aprobadas, 0 fallos, 0 errores y 0 omitidas. No se ejecutaron pruebas nuevas de la mejora (no está implementada), ni se repitió SQL autenticado. La ejecución anterior de Codex con verify tuvo 95 aprobadas/14 omitidas; distinguir ambos momentos. La verificación humana comunicada es solo JSON correcto del GET, no ejecución completa de Postman o pruebas de la baja. Colección existente validada como JSON sin agregar ruta inexistente; sin autenticación/CSRF. .env sigue ignorado/fuera del índice y no se abrió.

Documentación actualizada para mostrar propuesta y detención, manteniendo historial y AH-1/AH-2/AH-3. El usuario todavía no aprobó la migración ni las decisiones operativas propuestas. No se inventa aprobación, programación humana, error de código o ejecución. Sin commit/push/merge, React/sitio-publico sin cambios.

## Entrada 19 — Mejora humana aprobada: baja lógica de socios — 2026-11-08

Origen: el usuario detectó durante su revisión que había baja de inscripciones pero no de socios y propuso preservar los datos/historial al inactivar una persona. Tras evaluar alternativas, aprobó EstadoSocio, transacción y bajas de ACTIVAS, endpoint idempotente, confirmación MVC, tests y preparación de scripts. Expresamente prohibió ejecutar migraciones en ambas bases. Es decisión funcional humana y supervisión, no edición manual de código.

Implementación de Codex: EstadoSocio independiente de cuota, mapeo VARCHAR(8) con ACTIVO inicial; socio.estado/default/CHECK en schema de instalación nueva; SocioService con protocolo socio→actividades ordenadas y cambios gestionados/flush en una transacción; SocioInactivoException en el alta; API con estadoSocio, PUT de baja y codigo SOCIO_INACTIVO (regla null); confirmación MVC, estado visible y selector filtrado. No se permite reactivación vía formulario general ni se elimina físicamente. La cuota, fechas, vínculos y BAJAS previas no se modifican.

Migración preparada, no aplicada: database/migrations/001_estado_socio.sql. Guardas para socioclub/socioclub_test, tablas/columna/constraint, capturas antes/después, un ALTER aditivo y verificación de estructura/estados; repetición omite ALTER sin reactivar. DDL implica commits implícitos y no se promete ROLLBACK; se documentan backup y mantenimiento. No se conceden permisos a las cuentas de aplicación, ni se ejecuta respaldo/SQL/DDL desde Codex. Revisar antes de autorizar ejecución por catálogo.

Verificación automática real: verify offline sin MYSQL_TEST_DB_URL y flag false, BUILD SUCCESS; 128 casos, 111 aprobadas, 0 fallos, 0 errores y 17 omitidas. Ocho unitarias nuevas de baja, un rechazo de INACTIVO, cuatro REST y tres MVC nuevos aprobados; tres nuevos casos MySQL (baja/historial, rollback tras flush en transacción, carrera alta/baja) y seed ampliado solo compilaron. No se afirma rollback real o nuevo esquema MySQL aprobado. No se relajaron pruebas previas. Los resultados anteriores de 109 son históricos.

Colección actualizada a 22 requests, datos de ejemplo y campo/código nuevos; JSON validado sin Postman/runner. Documentación de implementación y operación en baja-logica-socios.md, propuesta original marcada histórica, maestro/API/MVC/guía actualizados. No se atribuyen a integrantes los cambios o correcciones de Codex. No se inventa prueba manual de la baja; la única comunicada anteriormente fue GET de actividades con JSON. React/sitio-publico intactos, .env sin leer. AH-1/AH-2/AH-3 mantienen su alcance. Sin commit/push/merge, se detiene para revisar código y migración.

## Entrada 20 — Auditoría y plan de migración segura — 2026-11-08

Solicitud humana: preparar ejecución verificable, primero en testing, con respaldo de ambas bases y revisión previa, sin ejecutar SQL ni modificar datos. Se conserva la propuesta humana original de baja lógica para preservar historial. No hay autorización para ejecutar la migración.

Trabajo de Codex: lectura completa de 001_estado_socio.sql; contraste con documentación oficial MySQL 8.4/Workbench; revisión del mapeo JPA, schema.sql, seed y fixtures, guardas de datasource y reportes Surefire. Hallazgos estáticos reales: la lista de dos catálogos no identificaba la intención del operador; el CHECK heredaba una comparación insensible a mayúsculas/acentos que podía aceptar valores incompatibles con el enum. No fueron fallos ejecutados contra MySQL.

Correcciones preparadas por Codex: exigir @sc_catalogo_esperado coincidente con DATABASE() antes del ALTER; comparar estado como BINARY tanto en migración como en schema.sql; ampliar la prueba SQL existente con valores no canónicos exigiendo SQLException 3819/HY000 y ck_socio_vigencia. No se atribuyen estas ediciones a un integrante humano. Se creó plan-migracion-estado-socio.md y se enlazó desde documentación de implementación, maestro y defensa: backups Workbench fuera de Git, prueba de recuperación aislada, consultas exactas, condiciones de detención y comandos de ensayo/desarrollo separados.

Evidencia: inspección de los diez XML existentes, 128 casos, 111 aprobados, 0 fallos, 0 errores, 17 omitidos. No se volvió a ejecutar Maven ni se afirma compilación o validación de las nuevas aserciones posteriores a esa evidencia. La sintaxis revisada no equivale a una prueba MySQL. Pendientes: respaldo y recuperación verificados, autorización por catálogo, ejecución en ensayo, 17 pruebas MySQL/verify y luego desarrollo si se aprueba. Revisión final de whitespace mediante git diff --check; el resultado se informa al cierre.

No se ejecutó SQL, backup, restauración, baja funcional ni migración. .env no fue leído; sin commit, push, merge o cambios React. AH-1/AH-2/AH-3 mantienen su alcance, no se registra un ajuste manual humano de código. Archivos vinculados: database/migrations/001_estado_socio.sql, database/schema.sql, PersistenciaMySqlTests.java y documentos de este plan; sin commit asociado todavía.

Cierre de revisión: rama feature/backend-api-rest confirmada; git diff --check sin errores de whitespace (Git informa normalización CRLF/LF); git check-ignore confirma backend/.env ignorado y git ls-files -- backend/.env no devuelve entradas. No se leyó su contenido. No se ejecutó Maven en esta auditoría.

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

### Trazabilidad adicional de supervisión humana

| ID | Necesidad y decisión humana | Ejecución humana comunicada | Trabajo de Codex | Evidencia y límites |
| --- | --- | --- | --- | --- |
| IH-MIG-01 | Baja lógica propuesta por el usuario para preservar historia; autorización por catálogo después de revisar/preparar el script | Respaldos mysqldump independientes, migración y validación de testing, verify 128/0/0/0 y luego migración de desarrollo por Workbench | SQL, Java/tests y documentación preparados; inspección posterior de reportes y plan manual | Confirmación humana y diez XML; sin dumps inspeccionados/restauración de prueba, sin consultas de desarrollo observadas, sin edición manual de fuente acreditada |

Relación con AH-1/AH-2/AH-3: utiliza la configuración y decisiones previas sin cambiar su autoría. Es una intervención operativa humana adicional verificable en el alcance comunicado; no acredita automáticamente un requisito de modificaciones manuales de código. Los archivos de la mejora y esta documentación siguen sin commit asociado; no se inventa trazabilidad a un hash nuevo.

### Auditoría del cierre documental posterior a la migración

Codex confirmó rama feature/backend-api-rest, staging vacío y cambios de API/baja/configuración de esquema/documentación aún sin publicar. Revisó la lista completa de 43 archivos nuevos/modificados publicables, patrones de tokens/claves privadas/credenciales JDBC y asignaciones literales de password/token/secret: no se detectaron candidatos; no es garantía absoluta de ausencia de secretos. .env.example contiene solo marcadores ficticios y usuarios locales esperados. backend/.env está ignorado y no figura en el índice; no se leyó. Sin temporales/logs/backups entre los cambios publicables. Colección y entorno Postman tienen JSON válido, sin runner ejecutado. git diff --check sin errores de whitespace; Git puede informar normalización CRLF/LF.

Regresión: revisión de las rutas API/MVC, delegación de inscripciones a InscripcionService, vigencia antes de RN1–RN5, edades inclusivas, confirmación GET sin mutación y POST de baja. Los diez XML suman 128 aprobadas/0 fallos/0 errores/0 omitidas. No se volvió a ejecutar Maven, Spring Boot, HTTP o SQL. No se identificó un bloqueo nuevo de código en esta revisión; el cierre y merge siguen pendientes de auditoría de desarrollo y recorrido manual documentado. No hay autenticación/autorización/CSRF y no se certifica seguridad para exposición pública. No se hicieron commits ni operaciones remotas.

## Incidente del fixture: CREATE PROCEDURE rechazado con 1064 — 2026-11-08

Intento humano real: cargar el fixture de baja lógica mediante Workbench en socioclub_test, servidor local 3306, conexión administradora. Resultado comunicado: error 1064 en CREATE PROCEDURE; datos no visibles y sin evidencia de INSERT exitosos. Codex revisó íntegramente el archivo, schema.sql y documentación oficial; no ejecutó SQL ni encontró todavía una línea inválida demostrable. Se solicitó mensaje completo «near ... at line ...» y modo de ejecución (archivo/selección). Causa pendiente; no se culpa al delimitador, permisos, orden de características o al usuario sin evidencia.

No se aplicó una modificación SQL especulativa ni se afirma corrección validada. Se evaluó eliminar el auxiliar, descartándolo para no perder rollback automático ante error de INSERT. Procedimiento de aislamiento de CREATE y SELECT de comprobación en validacion-manual-baja-socios.md. Aprendizaje: revisión estática no acredita aceptación por el parser del servidor; el texto exacto del error y la sentencia enviada son necesarios para diagnosticar. Código original generado por Codex; intento de ejecución del usuario. Sin cambio de bases, Maven, React, commit/push/merge por Codex.

### Evidencia ampliada y ajuste de delimitación del fixture

El usuario aportó el mensaje completo: «MySQL Error (1064) ... near '' at line 127», con siete sentencias informadas y un error. No indicó todavía si se envió todo el archivo o una selección. En el archivo previo al ajuste, CREATE comenzaba en la línea 20 y terminaba en la 146: el cierre END$$ era exactamente la línea 127 relativa al CREATE. La coincidencia es comprobable por lectura del archivo; no prueba por sí sola qué texto envió el cliente ni la causa raíz.

Interpretación limitada: el mensaje sitúa el rechazo al final del texto que el parser recibió. La hipótesis de delimitación/envío incompleto gana relevancia, pero no queda demostrada. No se identificó un DECLARE fuera de lugar ni se cambió el orden de características, permitido por la gramática. El número de sentencias reportado tampoco identifica por sí solo el programa cliente ni demuestra ejecución de CALL/INSERT.

Codex preparó un ajuste conservador de compatibilidad: DELIMITER // y cierre END // con separación explícita, manteniendo intactos el cuerpo, handlers, protección de catálogo, seis INSERT, IDs y resultados. $$ también es un delimitador válido cuando el cliente lo procesa correctamente; no se afirma que usarlo fuera un defecto de MySQL. Este cambio y el modo de envío deben validarse manualmente; no se registra el error como resuelto. No se ejecutó SQL.

Para el siguiente intento controlado, usar Workbench y una conexión nueva, consultar primero ausencia de rutina/datos con los SELECT anteriores. Si no hay auxiliar ni fixture, abrir el archivo actualizado y seleccionar desde SET NAMES utf8mb4 hasta DELIMITER ; inclusive después de END //, excluyendo CALL y DROP. Ejecutar la selección completa como script. No usar ejecutar sentencia actual dentro del cuerpo. DELIMITER es una directiva del cliente, no una sentencia que deba enviarse al servidor. Si esta fase falla, detenerse sin CALL y conservar el texto SQL efectivo que muestre el cliente junto con el error. Si la creación funciona, inspeccionar SHOW CREATE PROCEDURE socioclub_test.fixture_ensayo_baja_socio_v1; solo después, en esa misma conexión y con el catálogo correcto, ejecutar CALL del archivo y verificar las salidas. Retirar luego únicamente el auxiliar mediante el DROP PROCEDURE final. Si quedó un auxiliar de otro intento, detenerse y revisar su definición antes de retirarlo o reutilizarlo; no eliminarlo automáticamente.

Resultado esperado sin cambios: Martín Prueba Baja ACTIVO/AL_DIA, dos actividades con cupo máximo 5, una BAJA histórica en Yoga y dos ACTIVAS, una por actividad; cupo disponible 4 en ambas. La comprobación real y la causa definitiva siguen pendientes del siguiente resultado. Fuente: [ejemplo oficial de delimitación de procedimientos](https://dev.mysql.com/doc/refman/8.4/en/create-procedure.html).

## Cierre API y ensayo humano confirmado — 2026-11-08

El usuario confirmó carga del fixture mediante SQL convencional con transacción y COMMIT manual, y ensayo de baja correcto desde Secretaría con backend de testing en 127.0.0.1:8081. Se preservan como antecedentes los errores 1064 (near '' y luego near '//' en línea 127) al intentar el procedimiento. No existe evidencia suficiente para atribuir causa raíz definitiva al delimitador. La alternativa convencional funcionó según ejecución humana; Codex sustituyó database/fixtures/ensayo-baja-socio.sql para reflejarla, sin procedimientos/DDL, con guardas y decisión explícita COMMIT/ROLLBACK. Codex no ejecutó SQL ni alteró bases.

Pruebas completas válidas: 128 aprobadas, cero fallos/errores/omisiones, verify BUILD SUCCESS confirmado, ejecutado ANTES del fixture manual. Ahora socioclub_test contiene registros: NO repetir las suites que exigen vacío ni limpiar automáticamente. El ensayo correcto de Secretaría no demuestra por sí solo HTTP 409/SOCIO_INACTIVO, idempotencia, conservación exacta de filas, cupos finales o Postman; siguen pendientes de evidencia específica. La confirmación humana funcional es suficiente para continuar el cierre autorizado con estas limitaciones declaradas; no se inventa validación adicional.

Se mantienen IH-MIG-01 y AH-1–AH-3: migraciones/respaldos manuales y decisiones de configuración no equivalen automáticamente a ediciones de fuente. La alternativa SQL fue propuesta/aplicada por el usuario; el archivo versionable fue reescrito por Codex, no consta que el usuario lo editara. Sin autenticación/autorización/CSRF ni restauración de backups demostrada. Contrato y separación de capas en api-rest.md; fixture e instrucciones vigentes en validacion-manual-baja-socios.md. No es un despliegue público. Git se integra solo tras auditoría, sin PR y sin tocar main; hashes reales se registrarán después de ejecutarse.

## Integración React autorizada después del cierre backend — 2026-11-08

Decisión humana: integrar backend sin PR y comenzar React manteniendo el trabajo del equipo, sin tocar main/sitio-publico ni volver a ejecutar MySQL sobre testing poblado. Codex publicó feature/backend-api-rest (d58094c), integró sin conflictos por merge 3ace199, ejecutó 111 pruebas sin base con BUILD SUCCESS y publicó dev con registro 08d7c40. El verify completo humano 128/0/0/0 se conserva como evidencia anterior al fixture; no se simuló una ejecución nueva.

Rama feature/react-integracion-api desde 08d7c40. Código/decisiones de implementación de Codex: cliente Axios y validación DTO; cambios de imports en pantallas; contexto que conserva solo DNI; errores seguros por códigos; confirmación de baja y bloqueo de doble envío; redondeo RN6 en centavos. Mateo conserva autoría de componentes y commits previos; no se reescribió el frontend ni hubo ediciones manuales humanas nuevas confirmadas.

Problemas reales de esta ejecución: RN6 anterior redondeaba a pesos enteros y perdía centavos del DECIMAL backend; Codex corrigió y agregó dos tests (10.25 y bonificaciones), sin atribuirlo a ajuste humano. La primera corrida nueva tuvo 55 aprobadas/1 fallida de 56 por un findByText ambiguo: nombre repetido correctamente en lista y resumen. Se ajustó el test a buscar el botón, sin cambiar la funcionalidad para satisfacerlo. Segunda corrida con tests de centavos: 58 aprobadas, cero fallos/omisiones, cinco suites. Axios está simulado solo en tests; producción consume API y no usa datos locales. npm ci instaló el lock existente sin cambiar dependencias, con avisos de deprecación heredados de CRA. Build y prueba integrada se informan por separado; no se inventa un recorrido real ni CORS aprobado. Documentación: react-integracion-api.md. AH-1–AH-3/IH-MIG-01 sin alteraciones académicas.

## Resultado efectivo de validación React

npm.cmd test -- --watchAll=false --runInBand: 58 aprobadas, 0 fallidas, 0 omitidas, cinco suites, sin MySQL. npm.cmd run build con CI=true: exit 0, Compiled successfully (bundle JS gzip 80.08 kB, CSS 2.51 kB). El aviso Node DEP0176/fs.F_OK procede del stack de herramientas existente y no impidió compilar; no se migró CRA ni se cambiaron dependencias para ocultarlo. npm ci mantuvo package.json y package-lock.json sin modificaciones. Logs/resultados locales en .cache ignorado; build y node_modules no se publican. No hubo pruebas reales en navegador, solicitudes contra MySQL ni despliegue. Esos pendientes están descritos en react-integracion-api.md.

## Ampliaciones propuestas: FE-ADMIN-01 y RN8 — Prompt 21

El usuario propuso administración React con autenticación/rol ADMIN y prevención de conflictos horarios como EXTRAS, sin reemplazar RF1–RF10/RN1–RN7. Auditoría/diseño/SQL revisables en [propuesta-admin-rn8.md](propuesta-admin-rn8.md). Se comprobó que dias es texto libre, horario solo contiene inicio y no existe parser/duración; tampoco hay seguridad administrativa ni CSRF. No se consultaron datos vivos ni se asumieron horas de fin.

Codex preparó 002_cuenta_administrativa.sql y 003_actividad_horario.sql, sin ejecutarlas ni agregar código de aplicación o dependencias. La condición expresa del pedido obliga a detenerse porque la integración necesita alterar el esquema. Administración protegida y RN8 NO están implementadas todavía; la aplicación actual conserva sus limitaciones de seguridad. Pendiente aprobación humana del modelo, inventario real y horas fin, provisión privada de cuenta y pruebas de las funciones nuevas. Los éxitos anteriores de tests no acreditan estas EXTRAS. Propuesta humana; diseño/DDL/documentación Codex, sin ediciones manuales ficticias ni cambio de AH-1–AH-3/IH-MIG-01. Sin commit/push/merge ni modificación de bases.

Verificación efectiva de esta preparación: npm.cmd test -- --watchAll=false --runInBand ejecutado por Codex: cinco suites, 58 aprobadas, 0 fallidas y 0 omitidas (regresión EXISTENTE, no pruebas de FE-ADMIN-01/RN8). No se ejecutó Maven/MySQL ni build nuevo porque no cambió código de aplicación. git diff --check satisfactorio; .env sigue ignorado y no se leyó. El BOM local oficial spring-boot-dependencies 4.1.2 administra Spring Security 7.1.2; se contrastó su documentación, sin agregar la dependencia todavía. Git: seis documentos modificados y tres archivos nuevos (propuesta y dos SQL); sin commit/push/merge.

## Cierre obligatorio — decisión de alcance del usuario

Se postergan FE-ADMIN-01/RN8, sin implementarlas ni ejecutar su SQL. Se conserva auditoría; archivos reubicados en database/propuestas y detalles en [mejoras-futuras.md](mejoras-futuras.md). La decisión de priorizar requisitos/entrega es humana; reubicación/documentación de Codex. Las aprobaciones de tests anteriores son antecedentes, no pruebas nuevas de este cierre. No se borra trabajo del equipo.

## Prompt 22 — regresión e integración real de solo lectura

Ejecución actual de Codex: 58 pruebas React aprobadas (cinco suites, cero fallos/omisiones) y 111 backend seguras aprobadas (cero fallos/errores/omisiones, goal test BUILD SUCCESS). No se ejecutaron las suites MySQL ni verify completo: socioclub_test conserva los fixtures manuales. Los 128 anteriores son un antecedente distinto, no un resultado nuevo.

Consultas HTTP reales actuales, sin escrituras: GET / y /secretaria en backend 8080: 200; GET /api/actividades: 200 (9 actividades vivas); detalle de una actividad: 200, ID coincidente; consulta del socio ficticio 30111222 con vigencia/cuota válidas e inscripciones ACTIVAS como array: 200. GET actividad con ID no numérico: 400; ID 999999999: 404; frontend localhost:3000: 200. OPTIONS /api/inscripciones: 200 y Access-Control-Allow-Origin=http://localhost:3000, métodos GET/POST/PUT/DELETE/OPTIONS. No se imprimieron los datos personales completos ni se alteró una fila. Esto prueba transporte/contrato/preflight, no clics en navegador ni POST/PUT reales. 201/204/409 se validan con MockMvc; runner Postman/escrituras/revisión visual quedan pendientes. Ambos JSON Postman parseados correctamente. Las ocho actividades del sitio público serán las iniciales de la consigna, no un espejo en tiempo real de las nueve de la base viva.

Las nuevas propuestas EXTRA se postergan y preservan en database/propuestas; no son requisito para integrar React. El usuario autorizó merge directo sin PR y main intacta. Se publicará solo después de build y auditoría satisfactorios; hashes efectivos se registran al completar las operaciones.

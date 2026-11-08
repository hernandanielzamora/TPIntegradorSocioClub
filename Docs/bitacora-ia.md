# Bitácora de uso de IA

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

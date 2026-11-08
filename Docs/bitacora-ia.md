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

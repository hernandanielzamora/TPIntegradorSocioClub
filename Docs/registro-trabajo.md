# Registro de trabajo manual

## Confirmaciones humanas y cierre — 2026-11-08

El usuario solicitante confirmó que configuró MySQL 8.4 con Workbench, creó socioclub y socioclub_test y las cuentas locales socioclub_app/socioclub_tests. En el pedido de cierre confirmó tablas creadas mediante schema.sql y preparación manual de datos/pruebas. No aportó resultados definitivos de JPA, por lo que no se registra esa suite como aprobada ni se infiere ejecución de casos concretos.

El usuario autorizó explícitamente un commit y push en feature/backend-modelo-datos con el mensaje feat(backend): implement JPA data model and MySQL persistence, y PR hacia dev si fuera posible. Prohibió merge automático, cambios en main, force push y despliegue. Se trata de una decisión humana comunicada; la revisión del código, corrección del SQL, ejecución de validaciones y publicación se delegaron a Codex. No se atribuye ese trabajo automático al grupo ni se presume revisión manual.

El commit asociado se identifica por ese mensaje y el historial de esta rama. Al registrar esta decisión su ejecución/publicación todavía no se anticipan; el informe final confirma hash, push y estado de Git.

Destinado a intervenciones y decisiones humanas efectivamente comunicadas. Se distingue quién decide de quién ejecuta los cambios. No se atribuyen tareas a otros integrantes ni se presume revisión manual del código.

La generación y corrección automática se documenta en la [bitácora de IA](bitacora-ia.md).

| Fecha | Integrante | Tarea | Decisión | Modificación realizada | Motivo | Validación | Commit asociado (cuando exista) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 2026-11-02 | Usuario solicitante de esta sesión; sin atribución a otros integrantes | Preparación y autorización del primer commit y publicación | Normalizar texto a LF, conservar CRLF en .cmd y LF en Maven Wrapper Unix; ampliar protección de secretos; publicar en el remoto indicado si está vacío | Ejecución delegada a Codex por pedido explícito: .gitattributes, .gitignore y documentación; se mantiene el código funcional y sus versiones | Portabilidad, protección de credenciales y comienzo del historial compartido | Auditoría técnica previa realizada con Codex; consulta al remoto exitosa y sin referencias. No se afirma revisión manual del código. Control del índice y push pendientes al registrar esta decisión | Primer commit autorizado con mensaje chore: initialize SocioClub monorepo; aún no creado al escribir esta entrada. Identificable posteriormente con git log --all -- Docs/registro-trabajo.md |

Completar después de cada intervención real. En validación indicar comandos y resultados observados; si no se validó, consignarlo. Si no existe commit asociado, escribir «sin commit». No atribuir trabajo automático al grupo ni dar por realizada una revisión pendiente.

## Decisión humana comunicada — 2026-11-07

El usuario solicitante de esta sesión, sin identidad confirmada, autorizó explícitamente iniciar el servicio MySQL84 e indicó que configuraría las credenciales localmente. No consta ejecución manual ni revisión de código por su parte. Codex intentó Start-Service: Windows no permitió abrir el servicio, que permaneció detenido. No hay commit asociado. La implementación y las correcciones automáticas se registran en la entrada 5 de la bitácora de IA, sin atribuirlas a integrantes.

Validación técnica posterior al registro de la decisión, ejecutada por Codex: rama main y origin configurados; 66 archivos preparados y control git diff --cached --check aprobado; exclusiones y contenido del índice revisados sin indicios de secretos. Se preparó el permiso ejecutable del wrapper Unix para su uso en Linux. No constituye una revisión manual del código por parte del usuario. La fila conserva el estado observado al escribirla; el commit que incorpora este registro y su publicación se identifican en el historial de Git y en el informe final de la sesión.

## Ejecución humana comunicada — 2026-11-08

El usuario informó haber configurado las variables temporales en PowerShell y ejecutado Maven verify: 13 pruebas, 0 fallos, 7 errores, 0 omitidas, BUILD FAILURE. Las siete pruebas MySQL no iniciaron el contexto. Solicitó investigar sin exponer claves, modificar tablas ni publicar cambios. Codex diagnosticó los reportes y corrigió preventivamente la configuración de ensayo; esa intervención automática figura en la bitácora de IA. No consta una repetición autenticada exitosa. Sin commit asociado.

## Validación JPA comunicada — 2026-11-08

El usuario confirmó autenticación solucionada y ejecución de PersistenciaMySqlTests desde su terminal: 7 pruebas, 1 fallo, 0 errores, 0 omitidas. Autorizó corregir únicamente la falla de aserción si MySQL rechazaba correctamente los datos, sin modificar esquema ni datos. El análisis y ajuste automático se registran en la entrada 10 de la bitácora de IA. No consta todavía una repetición exitosa tras este ajuste. Sin commit asociado.

## Cierre humano confirmado y clasificación — 2026-11-08

El usuario solicitante confirmó corregir las contraseñas locales de MySQL y validar Maven verify con BUILD SUCCESS. Confirmó arranque completo: JDBC a jdbc:mysql://localhost:3306/socioclub, MySQL 8.4.13, EntityManagerFactory inicializado, Tomcat 8080 y Started SocioClubApplication. Codex inspeccionó los XML finales: 13 aprobadas, 0 fallidas, 0 errores, 0 omitidas. Esta inspección automática no se atribuye al grupo. No se recibió confirmación de HTTP GET /.

La decisión de incorporar .env para facilitar trabajo entre computadoras fue una propuesta humana expresa del usuario, quien definió los requisitos. Codex implementó la plantilla, el archivo inicial con marcadores y el cargador. El usuario completó/corrigió las contraseñas locales y ejecutó las validaciones. No se afirma que programó el cargador o el helper CHECK, ni que revisó manualmente su código. No consta la herramienta exacta empleada para corregir las claves ni la ejecución de los comandos MySQL de diagnóstico propuestos.

Posibles ajustes humanos de configuración: corrección de credenciales de ensayo y de desarrollo. La rúbrica no fue aportada y puede agruparlos como un único ajuste. Propuesta de .env: decisión humana. Maven y arranque: validaciones humanas. No hay evidencia suficiente para afirmar tres ajustes humanos distintos; queda pendiente académico, sin inventar intervenciones. Los seis incidentes y ocho campos de evidencia por caso se documentan en bitacora-ia.md. No se asocian a un commit nuevo porque no se realizó. El usuario pide recomendación de publicación, sin autorizar ejecución de commit, push o merge en este cierre.

## Tres ajustes humanos diferenciados — ampliación confirmada 2026-11-08

Esta ampliación actualiza la clasificación anterior con la confirmación detallada del usuario. No borra el historial ni atribuye programación automática al grupo.

| Ajuste | Acción humana confirmada | Naturaleza | Resultado y límite de evidencia |
| --- | --- | --- | --- |
| AH-1 | Revisó socioclub_tests en Workbench, restableció la contraseña y comprobó conexión con cliente MySQL | Configuración de testing | Conexión confirmada y siete tests JPA finalmente aprobados en XML. No se aportó línea exacta del cliente ni SQL de restablecimiento; no se inventan |
| AH-2 | Revisó/corrigió credenciales de socioclub_app y comprobó el arranque | Corrección operativa de desarrollo | Arranque completo confirmado, JPA inicializado y Tomcat 8080. HTTP GET / no confirmado |
| AH-3 | Propuso .env, plantilla y cargador para reutilizar configuración y definió requisitos | Decisión de diseño con implementación asistida | Codex implementó plantilla/script; el usuario completó claves y el log acredita carga. Pruebas del parser con datos ficticios fueron de Codex; no consta ensayo completo en otro equipo |

Los tres ajustes se vinculan con la tabla de trazabilidad de bitacora-ia.md, sección Trazabilidad de tres ajustes humanos. Las modificaciones Java y PowerShell fueron de Codex. No hay evidencia de tres correcciones manuales de código fuente si eso es lo requerido por la rúbrica; contabilizar tres ajustes en configuración/operación/diseño no garantiza aceptación académica. Posibles futuras correcciones manuales se registrarán solo cuando ocurran con evidencia antes/después, pruebas y autor real. Sin commit asociado a esta actualización; base publicada 5217d1c5e301bd129719d829255c149255e9a5d4.

## Autorización de publicación y corrección manual de plantilla — 2026-11-08

El usuario confirmó HTTP GET / con respuesta 200 y autorizó commit/push de feature/backend-modelo-datos y PR hacia dev, prohibiendo merge y cambios de main/dev. Ante la discrepancia de marcadores detectada por Codex, el usuario corrigió manualmente backend/.env.example a COMPLETAR_CLAVE_DESARROLLO/COMPLETAR_CLAVE_PRUEBAS y confirmó continuar. Codex verificó el archivo público sin abrir .env. Se registra como corrección real de configuración pública; no se inventa edición Java/PowerShell ni aceptación académica. La publicación se ejecuta por delegación explícita y su resultado se verifica al cierre. El commit de este registro se identifica por el mensaje fix(backend): validate MySQL persistence and document local setup; no se anticipa un hash antes de crearlo.

## Decisiones humanas de la etapa RN1–RN5 — 2026-11-08

El usuario solicitó implementar servicios/reglas/pruebas en feature/backend-reglas-negocio después de integrar el modelo mediante PR #3. Ante la diferencia entre el pedido inicial (mínima) y la consigna documentada (rango), confirmó aplicar mínimo y máximo inclusivos. También eligió continuar con mensajes claros de la consigna en vez de dejar su edición pendiente. No realizó una modificación manual de Java/PowerShell en esta etapa según la evidencia recibida; no se contabilizan estas decisiones como correcciones manuales del código.

Codex implementó y verificó: 19 tests unitarios nuevos aprobados; verify actual BUILD SUCCESS con 25 aprobadas, 0 fallos, 0 errores y 11 MySQL omitidas. El usuario aún debe ejecutar las pruebas autenticadas desde su terminal y aportar resultados; no se anticipan. Las oportunidades de revisión de fechas, errores y bloqueos se describen en reglas-negocio.md. Solo se registrará un nuevo ajuste manual cuando efectivamente se realice y valide. Sin commit asociado a esta etapa.

## Validación y autorización de cierre InscripcionService — 2026-11-08

El usuario confirmó todas las pruebas MySQL y verify exitosos, y autorizó auditoría, commit/push de feature/backend-reglas-negocio y preparación de PR hacia dev, sin merge. Codex corroboró 36 pruebas aprobadas en los XML Surefire con 0 fallos/errores/omisiones. La ejecución autenticada es humana y la inspección de reportes es automática. No se comunicó edición manual nueva de Java o de tests, por lo que no se agrega un ajuste humano ficticio. AH-1/AH-2/AH-3 conservan su alcance.

Implementación de Codex de la etapa y su autoría se mantienen en la entrada 13; cierre automático en entrada 14. La publicación autorizada se identifica por el mensaje feat(backend): implement transactional enrollment business rules y por el hash informado al completar la operación. Se revisará el PR antes de merge; no se autoriza cambiar main/dev directamente.

## Pedido humano de Secretaría MVC — 2026-11-08

El usuario solicitó implementar el módulo administrativo bajo /secretaria en feature/backend-mvc-thymeleaf, reutilizar el servicio existente, probar y documentar, sin publicar ni avanzar a REST. También pidió al menos dos oportunidades reales para revisión manual de código. Codex implementó Java, HTML, CSS, mensajes, pruebas y documentación; no se recibió una edición manual humana durante esta etapa y no se registra una como realizada.

Las correcciones de encoding y del test de entradas inválidas fueron automáticas y están en la entrada 15 de la bitácora. Verify ejecutado por Codex: 58 aprobadas, 0 fallos, 0 errores y 13 MySQL omitidas; BUILD SUCCESS. Las pruebas autenticadas y el recorrido en navegador quedan pendientes para la terminal del usuario. Puntos propuestos para revisar: InscripcionService.darDeBaja y ActividadService.guardar/eliminar. Se registrará cualquier intervención humana futura únicamente tras ocurrir y validarse. Sin commit asociado a esta implementación.

## Decisión humana: desarrollo individual y merges directos — 2026-11-08

El usuario informó que continuará individualmente y decidió mantener feature/* y dev sin crear PR obligatorios. Autorizó cerrar MVC mediante commit/push de feature y merge directo validado hacia dev, preferentemente con merge commit, sin force push, sobrescrituras, resolución automática de conflictos ni cambios de main. La autorización está condicionada a validación y confirmación necesaria; no se anticipa su ejecución.

El usuario no confirmó en este pedido un resultado nuevo de verify ni una revisión visual del módulo. Codex inspeccionó reportes locales con 71 aprobadas, 0 fallos/errores/omisiones y solicitó confirmación final de verify en su terminal. Esa inspección no se atribuye al usuario como trabajo manual. No se registra modificación manual nueva de código ni se cambia el alcance de AH-1/AH-2/AH-3. Sin commit de cierre creado en este punto.

## Confirmación de verify e integración autorizada — 2026-11-08

El usuario confirmó BUILD SUCCESS de Maven verify después de la auditoría. Codex volvió a inspeccionar los siete XML: 71 aprobadas, 0 fallos, 0 errores y 0 omitidas. Esto cierra la espera de confirmación de verify de las secciones anteriores. La ejecución autenticada fue del usuario; no se atribuye a Codex ni implica una revisión visual del navegador no comunicada.

Se continúa con el flujo individual autorizado: commit y push en feature/backend-mvc-thymeleaf, sincronización de dev, merge commit sin forzar y validación pertinente del árbol integrado antes de publicar origin/dev. Los hashes y resultados reales se informarán al completar cada operación; no se anticipa éxito en este registro. main queda fuera de alcance y la feature se conserva. No hay PR obligatorio.

Sin nuevas ediciones manuales de código confirmadas; AH-1/AH-2/AH-3 mantienen su alcance académico. Continúan los límites de seguridad: no hay autenticación/autorización ni protección CSRF implementada; el módulo no está preparado para exposición pública. La comprobación visual completa sigue pendiente. No se agregan funcionalidades ni se alteran bases o frontends.

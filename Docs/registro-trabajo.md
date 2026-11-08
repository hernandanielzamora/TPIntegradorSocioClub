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

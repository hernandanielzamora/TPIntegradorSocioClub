# Validación del modelo de datos — 2026-11-07

Estado final actualizado el 2026-11-08: 13 pruebas aprobadas, 0 fallos, 0 errores y 0 omitidas según XML Surefire; verify y arranque completo confirmados por el usuario. Ver la sección Cierre final confirmado al final del documento. Las entradas previas conservan el historial y no describen pendientes vigentes.

## Cierre inicial histórico — 2026-11-08 (superado por el cierre final)

El usuario autorizó guardar y publicar feature/backend-modelo-datos para continuar desde otra computadora, aun si la validación JPA queda pendiente. Confirmó MySQL operativo, bases/cuentas separadas, tablas creadas y preparación manual de datos/pruebas. No se inventan resultados de esa preparación: Codex no consultó sus conteos ni ejecutó JPA autenticado.

Credenciales de desarrollo y ensayo ausentes tanto en el proceso como en variables del usuario Windows al inspeccionar. No se solicitaron contraseñas ni se imprimieron secretos. Por esa falta de configuración segura se continuó el cierre de Git sin intentar conexión ni arranque completo.

| Comprobación ejecutada en el cierre | Aprobadas | Fallidas/errores | Omitidas | Resultado |
| --- | --- | --- | --- | --- |
| Maven verify | 6 | 0 | 7 | BUILD SUCCESS; JAR generado |
| Jest y React Testing Library | 20 | 0 | 0 | Tres suites aprobadas |
| Build React | No aplica | 0 | No aplica | Compiled successfully |
| git diff --check | No aplica | 0 | No aplica | Sin errores |

Maven verificó también el ajuste de inicialización SQL desactivada y el guard de base previo a cada test: el código compiló, pero las siete pruebas MySQL permanecieron omitidas. Las pruebas aprobadas del backend son cinco de Bean Validation y una MVC aislada; no acreditan persistencia.

Se encontró USE socioclub guardado en schema.sql y seed.sql. Codex retiró esas selecciones fijas de los archivos compartidos y añadió al test un rechazo de USE en seed.sql: de otro modo la carga de ensayo podía desviarse a desarrollo. No se modificaron las bases para hacer esa corrección.

Se revisaron fuentes nuevas y modificadas, exclusiones de Git y scripts. Se mantienen fuera de la entrega .cache, node_modules, target, build y credenciales. Los únicos SQL del cambio son esquema y datos sintéticos académicos; no se agrega un volcado de la base local. El resultado de revisión del índice se confirma antes del commit; el hash/publicación efectivos deben consultarse en Git y en el informe final.

Logs locales ignorados: .cache/cierre-backend-verify.log, .cache/cierre-react-build.log y .cache/cierre-react-tests.log. La ejecución Maven conserva advertencias de Mockito/Byte Buddy en Windows; React mostró una advertencia de deprecación de fs.F_OK. Ninguna impidió los resultados indicados; no se cambiaron versiones para silenciarlas.

Se agregó [instalación desde cero](instalacion-desde-cero.md), se consolidó [MySQL local](mysql-local.md) y se actualizó README. No se encontró un documento maestro adicional en los archivos locales versionables. README sigue siendo el índice de la documentación del repositorio.

Se prepara un único commit coherente del modelo/repositorios/SQL/pruebas/documentación, con el mensaje solicitado feat(backend): implement JPA data model and MySQL persistence, para publicar exclusivamente esta rama. GitHub CLI no está disponible; si no se crea PR automáticamente, la comparación manual debe usar base dev y origen feature/backend-modelo-datos. Publicar para revisión no implica aprobar persistencia ni autoriza merge.

Pendiente al retomar: configurar cuentas/credenciales en la otra máquina, revisar esquema y conteos reales, aprobar siete pruebas MySQL sin omisiones y arrancar Spring Boot con Hibernate validate. No se avanzó a RN1–RN5, REST, pantallas nuevas ni despliegue.

## Antecedentes de las intervenciones anteriores

**Nueva continuación, pendiente de preparación manual:** el usuario confirmó que creó socioclub y socioclub_test mediante Workbench, con socioclub_app para desarrollo y socioclub_tests con SELECT/INSERT/UPDATE/DELETE para ensayo. Confirmó que aún no ejecutó schema/seed. Se cambió la suite a spring.sql.init.mode=never: las tablas deben prepararse administrativamente en cada base; no hace falta ampliar permisos de la cuenta de ensayo. Se agregó una comprobación de SELECT DATABASE() antes de cada test. Estos últimos ajustes todavía no se compilaron ni ejecutaron: se espera confirmación manual antes de continuar, conforme a la instrucción del usuario. Las cifras siguientes corresponden a las ejecuciones anteriores.

**Estado vigente tras la segunda intervención:** MySQL84 Running y TCP 3306 accesible; autenticación pendiente por falta de credenciales disponibles. Verify final: 6 aprobadas, 7 omitidas, 0 fallos/errores. Los dos controles negativos fallaron expresamente con cero omisiones. No se crearon bases ni se validó persistencia/arranque real. El detalle actualizado está al final, en Segunda intervención; las secciones previas conservan el resultado histórico del modelado.

## Resultado y alcance

Implementación preparada en `feature/backend-modelo-datos`, sin commits, push, merge ni cambios en main. El árbol inicial estaba limpio sobre dev. Se ejecutó `git pull --ff-only origin dev` (Already up to date) y se creó la rama solicitada. La base contiene los merges `6f80dc4` (PR #1) y `a96635f` (PR #2). Se leyó el PDF de 14 páginas, README, requerimientos, escenarios de sistema, configuración y datos React antes de implementar.

No se modificaron `app-socios/`, `sitio-publico/`, versiones de dependencias, controladores ni vistas. No se implementaron RN1–RN5, REST ni integración Axios.

## Archivos creados y modificados

C = creado; M = modificado. Las rutas de paquetes Java son relativas a `ar/edu/siglo21/socioclub/`.

```text
README.md                                      M
backend/
  src/main/resources/application.properties    M
  src/main/java/ar/edu/siglo21/socioclub/
    model/
      Socio.java                               C
      Actividad.java                           C
      Inscripcion.java                         C
      EstadoCuota.java                         C
      EstadoInscripcion.java                   C
      package-info.java                        M
    repository/
      SocioRepository.java                     C
      ActividadRepository.java                 C
      InscripcionRepository.java               C
      package-info.java                        M
  src/test/java/ar/edu/siglo21/socioclub/
    model/IntegridadModeloTests.java            C
    repository/PersistenciaMySqlTests.java      C
database/
  schema.sql                                   M
  seed.sql                                     M
Docs/
  arquitectura.md                              M
  requerimientos.md                            M
  bitacora-ia.md                                M
  registro-trabajo.md                           M
  guia-defensa-tecnica.md                       C
  mysql-local.md                               C
  datos-modelo.md                               C
  validacion-modelo-datos.md                    C
```

Los artefactos de compilación e inspección permanecen ignorados en `backend/target/` y `.cache/`; no se incluyen en el código a entregar.

## Pruebas ejecutadas

Desde backend, con JDK 25.0.1 y las versiones existentes del pom:

```powershell
$env:MAVEN_USER_HOME = (Resolve-Path '..\.cache\maven').Path
.\mvnw.cmd -B '-Dmaven.repo.local=../.cache/m2' verify
```

Resultado final: **BUILD SUCCESS**, JAR generado, **13 pruebas descubiertas: 6 aprobadas, 7 omitidas, 0 fallos, 0 errores**.

| Clase | Aprobadas | Omitidas | Qué acredita |
| --- | --- | --- | --- |
| IntegridadModeloTests | 5 | 0 | Bean Validation real sobre instancias: DNI, obligatorios, email, nacimiento, horario, rangos, cupo positivo, precisión monetaria y relaciones obligatorias |
| SocioClubApplicationTests (existente) | 1 | 0 | Pantalla MVC inicial con Thymeleaf, sin JPA |
| PersistenciaMySqlTests | 0 | 7 | Preparadas, sin ejecutar; no acreditan persistencia |

Informes originales locales: `backend/target/surefire-reports/*.txt` y XML. Log final: `.cache/backend-modelo-verify.log`. Esos archivos están ignorados y no son documentación portable del repositorio; este documento conserva el resultado observado.

La ejecución mostró advertencias de autoacoplamiento Mockito/Byte Buddy y mensajes de apertura del agente JAR en esta ruta Windows con acentos. No impidieron la prueba MVC ni el build. No se modificaron dependencias ni se afirma que haya cobertura de mocks: no se añadieron mocks.

Se realizó una revisión estática reproducible de esta sesión con `python .cache/verificar-modelo.py`: ocho actividades idénticas a React; nombres, disciplinas y profesores presentes en PDF; 21 DNI únicos; seis identidades iniciales coherentes con React; dos MOROSOS; 19 vínculos; 18 ACTIVAS/1 BAJA; Yoga 14/15; edades y máximos coherentes a 2026-11-07. Se revisaron manualmente los demás valores de la tabla del PDF frente al seed. Este análisis de texto no ejecuta SQL ni demuestra sus restricciones.

`git diff --check` pasó. `git diff --quiet -- app-socios sitio-publico` confirmó ausencia de modificaciones a los frontends. Las pruebas/build React no se repitieron porque no se alteraron sus fuentes ni dependencias.

## Pruebas MySQL preparadas, pendientes

Tras la revisión de calidad, se activan con MYSQL_TEST_DB_URL configurada o MYSQL_TEST_ENABLED=true. La URL debe señalar exactamente socioclub_test; usan el esquema SQL real y Hibernate validate. Una conexión incorrecta produce error, no omisión:

1. Búsqueda por DNI, apellido sin distinguir mayúsculas, orden/paginación y disciplina.
2. BAJA y nueva ACTIVA del mismo socio/actividad, conteos y consultas que excluyen bajas.
3. Lectura SQL de BigDecimal/DECIMAL, LocalDate/DATE y FK de la inscripción.
4. Rechazo de DNI duplicado por la base.
5. Rechazo de FK inexistente.
6. Rechazo SQL de monto negativo y estado inexistente.
7. Ejecución repetida del seed: 8 actividades, 21 socios, 19 inscripciones, dos MOROSOS, Yoga 14/15; conservación de edición y baja sin recrearlas.

Todos los datos de prueba se revierten al finalizar cada método. El esquema crea tablas ausentes pero no las elimina. La prueba de seed requiere tablas sin registros previos; nunca borra para preparar la base. El usuario debe reservar socioclub_test para pruebas y evitar acceso concurrente.

## Estado de MySQL en la primera intervención

Servicio MySQL84 instalado, detenido, inicio manual. Puerto 3306 sin respuesta. El usuario autorizó iniciarlo; Start-Service falló porque Windows no permitió abrir el servicio desde este proceso. No fue un rechazo de revisión automática de aprobación. DB_USERNAME, DB_PASSWORD y MYSQL_TEST_DB_URL no estaban configuradas para esta sesión.

No se realizó conexión, carga inicial, arranque completo con JPA ni persistencia real. Tampoco se ejecutó Hibernate validate contra un esquema real; la compilación no valida JPQL ni la derivación de consultas durante el arranque. Las siete pruebas optativas están compiladas pero omitidas. Seguir [MySQL local](mysql-local.md) para cerrar esta validación.

## Decisiones, diferencias y siguiente paso

`Socio` representa la persona; `Actividad` define catálogo, límites y precio; `Inscripcion` conserva el vínculo y su estado histórico. Las FK viven en Inscripcion; los OneToMany son inversos, sin cascadas de borrado. No se impuso unicidad absoluta socio/actividad. Los DTO futuros evitarán ciclos de JSON. No se almacena cupoDisponible. La política de concurrencia queda para la etapa de servicios.

El seed prepara 21 personas para sostener catorce plazas de Yoga sin alterar las precondiciones de los seis casos del grupo. Fútbol comienza con una ACTIVA; PS02 requiere preparar un escenario lleno separado de PS03. Los contactos son de prueba. Ver [datos y contradicciones](datos-modelo.md).

La [guía de defensa](guia-defensa-tecnica.md) explica los doce conceptos solicitados, incluye diagrama Mermaid y preguntas sobre mappedBy, claves, repositorios, DTOs, BigDecimal, historial y límites de las pruebas.

**Próximo paso recomendado:** iniciar MySQL con permisos adecuados, configurar credenciales localmente y ejecutar las siete pruebas en socioclub_test. Luego cargar y revisar schema/seed en desarrollo y comprobar el arranque con validate. Solo después, en una intervención autorizada nueva, implementar servicios compartidos RN1–RN5 con transacciones y control de concurrencia.

## Segunda intervención: diagnóstico local y calidad de persistencia

Solicitud posterior del 2026-11-07: conseguir persistencia real sin agregar funcionalidades. Se conservó la rama y todos los cambios locales previos; no se actualizaron dev/main, no hubo commits, push, merge ni modificaciones en los frontends.

### Impedimento comprobado

Al inicio de esta intervención MySQL84 estaba Stopped y su arranque era Manual. Win32_Service identifica la cuenta NT AUTHORITY\NetworkService y el ejecutable/configuración descritos en mysql-local.md. mysql.exe existe fuera de PATH y reporta 8.4.13. my.ini señala puerto 3306, datadir en ProgramData y log DESKTOP-5Q7UPAT.err.

El puerto 3306 no tiene listener y Test-NetConnection devolvió False. El proceso no es administrador; el descriptor consultado con sc.exe sdshow no concede inicio a usuarios interactivos sin elevar. sc.exe query reporta Win32 1077 («No se ha intentado iniciar el servicio desde el último arranque») y código específico del servicio 0. El rechazo anterior ocurrió antes de iniciar el servidor; no demuestra una falla interna de MySQL.

El listado/lectura del directorio de datos fue denegado por Windows. Las consultas acotadas de eventos no devolvieron entradas MySQL en la muestra inspeccionada. No se modificaron ACL, cuenta del servicio, instalación ni datadir. Se solicitó al usuario ejecutar Start-Service desde PowerShell administrador; no hay resultado comunicado ni se atribuye al usuario una ejecución no confirmada.

En una comprobación posterior MySQL84 pasó a Running, con ExitCode/ServiceSpecificExitCode 0. Get-NetTCPConnection encontró listener en 3306 y Test-NetConnection a 127.0.0.1 devolvió True. No se recibió confirmación de quién realizó el inicio; no se atribuye como intervención humana comprobada. No se intentó eludir la elevación del servicio.

Las credenciales de desarrollo/ensayo siguen ausentes tanto en el entorno de proceso como en las variables del usuario Windows. Se solicitó configurarlas localmente sin compartirlas por chat. No se crearon ni inspeccionaron las bases socioclub/socioclub_test, cuyo contenido se desconoce. No se ejecutaron schema.sql ni seed.sql contra MySQL. Las tablas, restricciones y conteos siguen pendientes de validación real; el impedimento vigente es la autenticación, no el listener.

### Correcciones automáticas en las pruebas

1. MYSQL_TEST_DB_URL ahora activa PersistenciaMySqlTests aunque no exista MYSQL_TEST_ENABLED=true. Con una URL configurada, los errores de conexión/esquema/credenciales fallan en lugar de omitirse. Un valor de activación mal escrito también se rechaza. Sin URL ni activación se conserva la omisión explícita.
2. La suite admite MYSQL_TEST_DB_USERNAME/MYSQL_TEST_DB_PASSWORD, para usar una cuenta con permisos únicamente en ensayo sin sustituir las credenciales de desarrollo.
3. La prueba de tipos/relaciones hace flush, EntityManager.clear y findById antes de navegar socio y actividad. Comprueba además estados enum, fecha, monto e identidad de la base mediante SELECT DATABASE(). Estas aserciones están compiladas, pero no llegaron a ejecutarse en una conexión real.

### Ejecuciones observadas

| Ejecución | Aprobadas | Fallos de aserción | Errores | Omitidas | Resultado |
| --- | --- | --- | --- | --- | --- |
| Maven verify sin configuración de ensayo | 6 | 0 | 0 | 7 | BUILD SUCCESS; integridad en memoria y MVC aprobados; MySQL no probado |
| Solo PersistenciaMySqlTests, URL terminada en socioclub, sin flag de activación | 0 | 0 | 7 | 0 | BUILD FAILURE esperado; IllegalArgumentException rechaza la base antes de conectar |
| Solo PersistenciaMySqlTests, URL socioclub_test en puerto local 65534 sin listener, sin flag | 0 | 0 | 7 | 0 | BUILD FAILURE esperado; Communications link failure / Connection refused |

Los dos controles negativos se ejecutaron con configuración sintética local y sin usar credenciales reales. Los siete errores de cada ejecución corresponden al contexto Spring que no pudo inicializarse (y su umbral de reintentos); no representan siete operaciones de persistencia ejecutadas ni aprobadas. Sirven para demostrar activación automática y rechazo visible. No se conectó a ninguna base durante esos controles.

Logs locales ignorados: .cache/persistencia-diagnostico-verify.log, .cache/persistencia-guardia-base.log y .cache/persistencia-conexion-rechazada.log. El verify normal se repite después de los controles negativos para dejar Surefire y el JAR en su estado de validación sin MySQL.

Se revisó nuevamente schema/seed mediante la comprobación estática existente; no hubo cambios en sus datos o restricciones en esta intervención. Eso no sustituye ejecutar SQL. Las entidades/repositorios compilan; sus consultas y mapeos solo podrán validarse completamente cuando Spring conecte con el servidor.

### Arranque y disponibilidad para revisión

No se intentó presentar un arranque deliberadamente sin credenciales como validación de Spring Boot: el arranque completo con Hibernate validate y la respuesta HTTP de la aplicación real siguen pendientes. La prueba MVC aislada aprobada no los sustituye.

En esta intervención se modificaron únicamente PersistenciaMySqlTests.java y los cuatro documentos solicitados: validacion-modelo-datos.md, mysql-local.md, guia-defensa-tecnica.md y bitacora-ia.md. No hubo nuevas funcionalidades ni versiones modificadas.

**La rama no está lista para afirmar persistencia validada ni para dar por aprobado su PR hacia dev.** El servidor ya está Running y TCP responde. Antes de aprobar commit/PR faltan las cuentas/credenciales locales, carga/verificación real, siete pruebas MySQL aprobadas y arranque completo. No se hicieron commits ni push.

## Diagnóstico de autenticación — 2026-11-08

El usuario ejecutó Maven verify desde su terminal: 13 pruebas, 6 aprobadas, 0 fallos, 7 errores, 0 omitidas; BUILD FAILURE. Codex inspeccionó los reportes locales sin ejecutar esa conexión.

Evidencia: TEST-ar.edu.siglo21.socioclub.repository.PersistenciaMySqlTests.xml, líneas 170–174 y 308: ErrorCode 1045 / SQLState 28000; AuthException causada por java.sql.SQLException: Access denied for user 'socioclub_tests'@'localhost' (using password: YES). El rechazo de autenticación precede al fallo de metadatos/dialecto. El umbral de ApplicationContext evita los siguientes intentos: no son siete fallos independientes de consultas JPA. El esquema y los cuerpos de las pruebas no llegaron a validarse.

No se puede distinguir con este reporte entre contraseña incorrecta, cuenta ausente/no coincidente u otra configuración de autenticación. No se cambian dialecto, permisos, tablas ni scripts. Codex corrigió únicamente el fallback de credenciales: al activarse la suite, MYSQL_TEST_DB_USERNAME y MYSQL_TEST_DB_PASSWORD son obligatorias y se registran siempre mediante DynamicPropertySource. Nunca se heredan DB_USERNAME/DB_PASSWORD. Esta protección no soluciona por sí sola el rechazo 1045.

Procedimiento pendiente: volver a ingresar la clave de ensayo con Read-Host -AsSecureString en la misma terminal; contrastar el acceso con mysql.exe --protocol=TCP --host=localhost --port=3306 --user=socioclub_tests --password --database=socioclub_test. --password sin valor pide la clave oculta. Ejecutar únicamente SELECT DATABASE(), CURRENT_USER(); SHOW GRANTS FOR CURRENT_USER; y conteos SELECT COUNT(*) de las tres tablas. Si vuelve a fallar con 1045, revisar cuenta/autenticación con el administrador local; no se autoriza un cambio automático de permisos o contraseña.

Desde backend, repetir .\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests' test. Tras aprobar las siete, ejecutar .\mvnw.cmd -B verify. Con DB_URL de socioclub y DB_USERNAME socioclub_app, iniciar .\mvnw.cmd spring-boot:run y comprobar desde otra terminal HTTP GET / (puerto 8080 por defecto). No atribuir éxito hasta observar los resultados. No hay validación autenticada posterior al ajuste; PR pendiente.

## CHECK de MySQL y aserción JDBC — 2026-11-08

El usuario informó autenticación solucionada y ejecutó únicamente PersistenciaMySqlTests: 7 pruebas, 6 aprobadas, 1 fallida, 0 errores, 0 omitidas; BUILD FAILURE. Codex comprobó el reporte Surefire: rechazaEstadosYMontosInvalidosDesdeSql falló en la primera aserción (línea 179 antes del ajuste). UPDATE actividad SET cuota_mensual = -1 WHERE id = ? fue rechazado por ck_actividad_cuota (CHECK cuota_mensual >= 0), con SQLException código 3819 y SQLSTATE HY000. Spring la envolvió en UncategorizedSQLException, no en DataIntegrityViolationException. La restricción de monto sí funcionó; el escenario posterior de estado no llegó a ejecutarse en esa corrida.

Corrección automática mínima: ambos escenarios conservan su SQL y ahora comprueban una DataAccessException cuya causa más específica debe ser SQLException, con código 3819, SQLSTATE HY000 y nombre exacto de la restricción entre comillas: ck_actividad_cuota o ck_socio_estado. Se comprueba el rechazo CHECK real sin depender de la categoría traducida por Spring. No se acepta cualquier excepción. El rechazo de estado por ck_socio_estado (CHECK estado_cuota IN ('AL_DIA', 'MOROSO')) debe confirmarse en la repetición; no se presenta como observado.

No se modificaron tablas, schema.sql, seed.sql, configuración de producción ni dependencias. Repetición pendiente desde backend en la terminal PowerShell del usuario con sus variables temporales: .\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests#rechazaEstadosYMontosInvalidosDesdeSql' test; después .\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests' test. El proceso de Codex no hereda esas credenciales. No se afirma aprobación posterior al ajuste.

## Entorno local reutilizable — 2026-11-08

El usuario confirmó Maven verify completo con BUILD SUCCESS. No aportó conteos nuevos ni se repitió esa ejecución desde Codex; no se infieren resultados del arranque HTTP. Se implementó backend/.env.example con las siete variables JDBC/test y PORT/CORS opcionales, backend/.env local solo con marcadores, y scripts/load-env.ps1 sin dependencias. git check-ignore confirmó exclusión del archivo local; la plantilla no está ignorada y .env no está versionado. No fue necesario cambiar .gitignore.

El cargador solo modifica variables del proceso PowerShell. Valida variables obligatorias, claves de ejemplo, cuentas/bases separadas y flag antes de cargar; trata contenido como texto literal sin ejecutar código ni mostrar valores. Pruebas automáticas con una copia ficticia en .cache: carga desde otra ruta, # y =, caracteres PowerShell y comillas conservados, variables faltantes rechazadas y root rechazado sin carga parcial. No se leyeron claves locales ni se ejecutó MySQL/Maven con ellas. application.properties y las protecciones de PersistenciaMySqlTests se revisaron y se conservaron sin cambios en esta intervención. Procedimiento completo en mysql-local.md. Sin cambios de SQL, bases, dependencias o publicación.

## Cierre final confirmado — 2026-11-08

Este cierre reemplaza los estados pendientes de las entradas anteriores; estas se conservan como historial de diagnóstico. El usuario confirmó corrección manual de las contraseñas locales, Maven verify con BUILD SUCCESS y arranque completo de Spring Boot: JDBC a jdbc:mysql://localhost:3306/socioclub, MySQL 8.4.13, EntityManagerFactory inicializado, Tomcat en 8080 y Started SocioClubApplication. Codex no leyó .env ni ejecutó ese arranque autenticado.

Los reportes XML locales de backend/target/surefire-reports acreditan:

| Suite | Aprobadas | Fallidas | Errores | Omitidas |
| --- | ---: | ---: | ---: | ---: |
| IntegridadModeloTests | 5 | 0 | 0 | 0 |
| PersistenciaMySqlTests | 7 | 0 | 0 | 0 |
| SocioClubApplicationTests | 1 | 0 | 0 | 0 |
| Total | 13 | 0 | 0 | 0 |

Los XML corroboran las pruebas; BUILD SUCCESS de verify y el arranque son confirmaciones humanas. No se confirmó todavía respuesta HTTP GET / ni se volvieron a consultar los conteos de desarrollo desde Codex. No se necesita repetir pruebas autenticadas para esta actualización documental.

Los errores 1045/28000 de socioclub_tests y socioclub_app fueron rechazos de autenticación. El usuario confirmó corregir las contraseñas locales; Codex no cambió cuentas ni permisos. Los mensajes de dialecto y umbral del contexto eran consecuencias. El rechazo CHECK 3819/HY000 era correcto: Codex corrigió la aserción que esperaba una categoría de Spring distinta, manteniendo los dos escenarios y verificando causa SQL, código, SQLSTATE y nombre de restricción. La suite final acredita ambos escenarios.

La propuesta de .env para facilitar el trabajo entre computadoras fue del usuario. Codex implementó .env.example y scripts/load-env.ps1; el usuario completó/corrigió sus claves locales y validó los resultados. El archivo local fue creado inicialmente solo con marcadores. Se revisó el cargador con datos ficticios, sin leer .env. No cambia variables permanentes ni evalúa el contenido como PowerShell. Ver [uso local](mysql-local.md) y [registro detallado de incidentes](bitacora-ia.md#cierre-de-incidentes-y-supervisión-humana--2026-11-08).

Auditoría Git: rama feature/backend-modelo-datos, base publicada 5217d1c5e301bd129719d829255c149255e9a5d4. git check-ignore -v backend/.env identifica .gitignore:6; git ls-files --stage -- backend/.env no devuelve entradas; git log --all -- backend/.env no devuelve commits. No hay evidencia de versionado ni inclusión actual en el índice. Git no permite demostrar retrospectivamente que nunca se haya agregado y retirado del índice sin commit. .env.example está permitido. El diff de código contiene exclusivamente la exigencia de credenciales de ensayo y las aserciones CHECK; también se revisaron la plantilla y el cargador nuevo. No se encontraron secretos ni temporales entre los archivos publicables revisados. Los reportes target y fixtures .cache permanecen ignorados. No hubo cambios de SQL, bases, dependencias o funcionalidades.

Recomendación: apto técnicamente para preparar commit y push de esta etapa; merge a dev después de revisión del PR y de comprobar HTTP GET / si se exige cerrar también la validación MVC real. Actualización académica: el usuario confirmó detalles adicionales y se registran AH-1, AH-2 y AH-3 como tres ajustes humanos de configuración, operación y diseño; no se acreditan tres correcciones manuales de código fuente si la rúbrica exige específicamente eso. Ver la tabla de trazabilidad de bitacora-ia.md. No se inventan intervenciones ni se atribuye código de Codex al grupo. documento-maestro-proyecto.md no existe y no se crea para este cierre. No se ejecutó commit, push o merge.

## Publicación para revisión — 2026-11-08

El usuario confirmó manualmente HTTP GET / con respuesta 200, además de verify BUILD SUCCESS y arranque completo contra socioclub. Esta confirmación cierra el pendiente HTTP de los registros históricos; Codex no ejecutó esa consulta. Surefire vuelve a corroborar 13 aprobadas, 0 fallos, 0 errores y 0 omitidas. Se mantiene el alcance académico de AH-1/AH-2/AH-3, sin atribuir programación automática al grupo.

Durante la auditoría, Codex detectó que los marcadores CLAVE_DESARROLLO/CLAVE_PRUEBAS de la plantilla no coincidían con los que rechaza el cargador. Se detuvo la publicación; el usuario confirmó y realizó la corrección manual de backend/.env.example a COMPLETAR_CLAVE_DESARROLLO/COMPLETAR_CLAVE_PRUEBAS. Codex comprobó el archivo público y su alineación con el script, sin abrir .env. Esta corrección de configuración pública se registra como intervención humana adicional, sin afirmar aceptación por la rúbrica ni reescribir los tres ajustes anteriores.

El usuario autorizó commit y push únicamente de feature/backend-modelo-datos y preparación/creación de PR hacia dev, sin merge ni modificaciones de dev/main. La operación se realizará después de revisar staging; su hash, publicación y enlace se informan al terminar, sin anticipar éxito en este registro. No hay dependencias, funcionalidades, migraciones o datos nuevos. El PR queda sujeto a revisión humana antes del merge.

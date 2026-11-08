# Validación del modelo de datos — 2026-11-07

## Cierre vigente — 2026-11-08

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

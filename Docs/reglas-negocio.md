# Inscripciones: reglas RN1–RN5 — 2026-11-08

Rama de trabajo: feature/backend-reglas-negocio, desde f0633c0 (merge del PR #3). Implementación de Codex por pedido del usuario; cierre autorizado para commit/push y PR, sin merge ni cambios a main/dev. La etapa anterior de persistencia cerró con 13 pruebas y HTTP 200; esos resultados son históricos y no acreditan por sí solos el servicio nuevo.

## Responsabilidad y métodos

`InscripcionService.inscribir(Long socioId, Long actividadId)` es el punto de alta que compartirán los futuros controladores MVC y REST. Busca socio y actividad, valida las reglas y guarda una inscripción ACTIVA con la fecha actual. Devuelve la entidad; la conversión a DTO y la traducción a HTTP corresponden a una etapa futura. No hay nuevos controladores, endpoints, baja, ABM ni cálculo de cuotas en esta entrega.

Los métodos privados `validarSocio` y `validarInscripciones` separan condiciones personales de consultas de vigencia/cupo. `FechaConfig.reloj()` proporciona Clock para usar la zona del proceso en producción y una fecha fija en las pruebas. La fecha no la envía el cliente.

| Regla | Comprobación | Consulta o dato | Código de rechazo |
| --- | --- | --- | --- |
| RN1 | Activas de la actividad menores que cupoMaximo | contarActivasDeActividad, JPQL existente; BAJA queda fuera | SIN_CUPO |
| RN2 | No existe otra ACTIVA del mismo socio/actividad | existsBySocioIdAndActividadIdAndEstado con ACTIVA | INSCRIPCION_DUPLICADA |
| RN3 | Edad en años completos dentro de mínimo y máximo inclusivos | Period.between(fechaNacimiento, hoy).getYears() | EDAD_FUERA_DE_RANGO |
| RN4 | Estado distinto de MOROSO | socio.estadoCuota | SOCIO_MOROSO |
| RN5 | Menos de tres inscripciones ACTIVAS del socio antes del alta | countBySocioIdAndEstado con ACTIVA | MAXIMO_ACTIVIDADES |

El usuario confirmó expresamente aplicar mínimo y máximo según la consigna, porque el pedido inicial solo mencionaba mínima. No se agregó una regla arbitraria. RN3 utiliza años cumplidos: el día anterior al cumpleaños no se alcanza la edad siguiente. Para nacimiento 29/02, Period cumple años el 01/03 en un año no bisiesto; el test documenta esa convención técnica, sin atribuirle un significado legal. No se calcula la edad dividiendo días por 365.

Una reinscripción crea una fila nueva y conserva cualquier BAJA previa. No se modifica el historial ni se agrega una restricción UNIQUE que impida bajas y reinscripciones.

## Errores y orden de evaluación

`RecursoNoEncontradoException` informa socio o actividad inexistente. `ReglaNegocioException` incluye un Codigo estable y un mensaje para el usuario. Las excepciones son RuntimeException y provocan rollback. Errores de conexión, bloqueo o integridad no se convierten en rechazos de negocio: deben investigarse como fallos técnicos. Los IDs nulos se rechazan antes de consultar mediante requireNonNull.

Primero se comprueba existencia (socio y actividad), luego morosidad, edad, duplicado, máximo del socio y cupo. Si hay varios motivos, se informa el primero. El orden no agrega reglas: prioriza errores y evita guardar ante cualquier rechazo. Los tests comparan códigos, no textos, para que una mejora de redacción no altere el contrato de negocio. Los mensajes se basan en la consigna; el usuario eligió continuar con esos mensajes y no se registra una edición humana que no ocurrió.

## Transacciones y solicitudes simultáneas

Contar y luego insertar sin coordinación permite que dos solicitudes vean el mismo lugar libre. `@Transactional(isolation = READ_COMMITTED)` abarca búsqueda, validación y alta. Las consultas nuevas `buscarParaInscribir` de SocioRepository y ActividadRepository usan `@Lock(PESSIMISTIC_WRITE)` y JPQL por ID. MySQL/InnoDB conserva esos bloqueos hasta commit/rollback.

Se bloquea siempre primero socio y después actividad. El bloqueo de socio serializa dos altas de la misma persona, aun en actividades diferentes (RN2/RN5). El bloqueo de actividad serializa altas de socios distintos por el mismo último cupo (RN1). READ_COMMITTED evita reutilizar una vista antigua al contar después de que otra transacción confirmó. No basta marcar el método como transaccional sin bloquear. Ningún método llama al alta mediante autoinvocación: los consumidores deben usar el bean Spring para activar el proxy transaccional.

No se requiere cambiar el esquema. La garantía supone que todas las nuevas altas pasan por el servicio; INSERT directos, un futuro servicio de edición de cupos o cambios de estado que omitan el protocolo no están cubiertos. Los futuros escritores deben respetar el orden/protocolo. No se afirma ausencia total de deadlocks: MySQL puede detectar esperas y abortar una transacción. No se agregó reintento automático ni se silenciaron errores de bloqueo.

## Pruebas y resultados reales — cierre actualizado

| Suite actual | Casos | Aprobadas aquí | Fallos | Errores | Omitidas aquí |
| --- | ---: | ---: | ---: | ---: | ---: |
| InscripcionServiceTests (Mockito, sin MySQL) | 19 | 19 | 0 | 0 | 0 |
| IntegridadModeloTests | 5 | 5 | 0 | 0 | 0 |
| SocioClubApplicationTests (MVC aislada) | 1 | 1 | 0 | 0 | 0 |
| PersistenciaMySqlTests (existentes) | 7 | 7 | 0 | 0 | 0 |
| InscripcionServiceMySqlTests (nuevas) | 4 | 4 | 0 | 0 | 0 |
| Total de verify | 36 | 36 | 0 | 0 | 0 |

Primera ejecución de Codex (antecedente histórico): Maven verify BUILD SUCCESS sin activar MySQL; JAR generado. El resultado final autenticado figura en la tabla actualizada y en Auditoría final para PR. Se utilizó Maven 3.10.0 ya instalado por el wrapper, en modo offline con su caché. El primer intento no pudo abrir archivos de bloqueo de la caché fuera del sandbox; la repetición autorizada fuera del sandbox ejecutó las pruebas. No se modificó el wrapper ni se agregaron dependencias. Mockito emitió una advertencia de carga dinámica de agente con Java 25; no fallaron las pruebas y no se cambió configuración por esa advertencia. Reportes locales en backend/target/surefire-reports (se sobrescriben al repetir).

Las 19 pruebas unitarias cubren: alta válida y orden de llamadas; último cupo; cupo completo/excedido; duplicado activo; fila nueva tras BAJA; un día antes, durante y después del cumpleaños mínimo; máximo inclusivo y excedido; 29/02; morosidad; aceptación con dos activas; rechazo con tres/cuatro; socio/actividad inexistentes e IDs nulos. Los rechazos comprueban que no se guarda.

Las cuatro nuevas pruebas MySQL comprueban: BAJA que no ocupa cupo ni impide una tercera actividad y conserva historial; dos socios por el último cupo; duplicado simultáneo del mismo socio; dos altas del mismo socio en actividades distintas compitiendo por el tercer lugar. Los trabajadores salen desde una barrera y cada llamada usa una transacción real del servicio; se exige un éxito y el código de rechazo correspondiente, más el conteo persistido final. Las cuatro pruebas aprobaron según los XML finales inspeccionados por Codex; la ejecución autenticada fue realizada por el usuario. La omisión descrita anteriormente corresponde a la primera corrida sin credenciales.

### Precauciones particulares de las pruebas MySQL nuevas

Reutilizan la activación y DynamicPropertySource de PersistenciaMySqlTests: URL exclusivamente socioclub_test y credenciales MYSQL_TEST_DB_USERNAME/PASSWORD obligatorias, sin fallback. Antes de crear datos verifican SELECT DATABASE() y exigen las tres tablas vacías; si encuentran datos, fallan sin borrarlos. No ejecutan schema/seed ni DDL. No ejecutar las suites en paralelo ni permitir escritores ajenos durante estos ensayos.

Las pruebas concurrentes necesitan datos confirmados visibles entre conexiones. Por eso no usan rollback global por método: crean fixtures con commit y, al finalizar los trabajadores, eliminan solamente inscripciones/socios/actividades de los IDs creados por ese test, con nueva comprobación de catálogo. También la prueba de BAJA usa este esquema. No borran datos para vaciar una base preexistente. AUTO_INCREMENT puede avanzar. Una interrupción abrupta puede dejar fixtures: detenerse e inspeccionar en vez de vaciar tablas automáticamente. La suite previa conserva su rollback original.

## Comandos desde PowerShell del usuario

Desde backend, primero se puede comprobar la lógica sin cargar claves:

```powershell
.\mvnw.cmd -B '-Dtest=InscripcionServiceTests' test
```

Para validar persistencia y concurrencia con las variables locales, en esa misma terminal:

```powershell
.\scripts\load-env.ps1
if ($?) {
    .\mvnw.cmd -B '-Dtest=PersistenciaMySqlTests,InscripcionServiceMySqlTests' test
}
# Después de verificar que las 11 pruebas MySQL aprueban sin omisiones:
.\mvnw.cmd -B verify
```

La ejecución final ya acredita 36 aprobadas y 0 omitidas; estos comandos permiten reproducirla con la configuración local. No compartir .env ni imprimir claves. Codex no leyó .env, no heredó las variables de la terminal del usuario y no ejecutó SQL autenticado en esta etapa.

## Revisión humana y defensa

Decisiones importantes para revisar: rango de edades y convención de cumpleaños; precedencia de errores; orden socio/actividad de bloqueos y alcance de READ_COMMITTED; limpieza de fixtures de concurrencia. Son revisiones justificadas, no errores fabricados. Una modificación solo se registrará como humana si el integrante realmente la hace en VS Code y se identifica su diff y prueba. En esta etapa el usuario confirmó el rango completo y continuar con mensajes de la consigna: son decisiones, no ediciones manuales del código.

Preguntas de defensa: ¿por qué el servicio y no el controlador aplica reglas? Para compartirlas entre MVC y REST. ¿Por qué una transacción no basta para el último cupo? Dos transacciones pueden contar antes de insertar; el bloqueo serializa. ¿Por qué bloquear socio además de actividad? Para proteger el máximo de tres entre actividades distintas. ¿Por qué no borrar bajas? Son historial y no cuentan como activas. ¿Los mocks prueban bloqueos? No: hacen falta MySQL y conexiones reales.

Cierre: el usuario confirmó las pruebas MySQL y verify exitosos; los XML finales acreditan las 36 aprobadas sin fallos, errores u omisiones. Las tres pruebas de solicitudes simultáneas y la prueba de BAJA aprobaron. Pendiente: revisión humana del PR y mejoras manuales solo si se justifican. Controladores, DTOs y nuevas funcionalidades requieren otro pedido.


## Auditoría final para PR — 2026-11-08

El usuario confirmó ejecución MySQL y Maven verify BUILD SUCCESS. Codex inspeccionó los cinco XML Surefire: 19 unitarias del servicio, 5 de integridad, 1 MVC, 7 de persistencia y 4 de integración del servicio: 36 aprobadas, 0 fallos, 0 errores, 0 omitidas. La primera corrida offline de 25 aprobadas/11 omitidas se conserva como antecedente, no como resultado vigente. No se repitieron pruebas autenticadas ni se abrió .env en este cierre.

Auditoría de diseño: RN1-RN5 permanecen en Service, rango inclusivo con Period/Clock, excepciones diferenciadas y orden socio/actividad PESSIMISTIC_WRITE bajo READ_COMMITTED. No se detectó un impedimento técnico para revisión. La garantía exige usar el bean Spring, iniciar el alta con esta transacción (no heredar una transacción externa de aislamiento incompatible) y que futuros escritores respeten el protocolo. Las pruebas de carrera son escenarios reproducibles, no una demostración exhaustiva de todos los intercalados. Los errores de bloqueo siguen siendo técnicos y no se ocultan.

La configuración de pruebas obliga socioclub_test y credenciales exclusivas; verifica catálogo y tablas vacías, y limpia solo IDs propios. No se modificó socioclub. .env permanece ignorado y fuera del índice; no se inspeccionó su contenido. No hay cambios de esquema, dependencias, frontends o controladores. Commit/push y PR fueron autorizados por el usuario; merge no autorizado. La publicación se identifica por el mensaje feat(backend): implement transactional enrollment business rules. El resultado real y hash se informan al cierre, sin anticiparlos aquí.

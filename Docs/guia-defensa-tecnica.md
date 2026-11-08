# Guía de defensa técnica — modelo y persistencia

Estado de cierre 2026-11-08: Maven verify aprobó 6 pruebas y omitió 7 MySQL por falta de configuración segura para Codex; React aprobó 20 pruebas y su build. El usuario confirmó preparación manual del servidor/bases/tablas/datos, pero falta verificar persistencia y arranque de la aplicación. La publicación de la rama conserva ese pendiente. Ver [instalación en otra computadora](instalacion-desde-cero.md).

Alcance de esta intervención (2026-11-07): entidades, repositorios, esquema y datos preparados. No hay servicios de inscripción ni API REST implementados. Las pruebas MySQL requieren un servidor operativo; no confundir preparación con ejecución.

## Conceptos con ejemplos del proyecto

1. **JPA** es la especificación de Java para persistir objetos y definir sus relaciones mediante anotaciones. Usamos `jakarta.persistence`. Hibernate es la implementación que incorpora Spring Data JPA.
2. Un **ORM** relaciona objetos con filas de tablas. Hibernate transforma, por ejemplo, un `Socio` en una fila de `socio`; evita escribir manualmente cada INSERT y SELECT.
3. Una **entidad** es una clase persistente con identidad. `@Entity` marca `Socio`, `Actividad` e `Inscripcion`; `@Table` fija nombres SQL. El constructor vacío permite que JPA reconstruya objetos.
4. Una **clave primaria** identifica una fila. `Long id`, `@Id` y `@GeneratedValue(IDENTITY)` corresponden a `BIGINT AUTO_INCREMENT`. El DNI tiene unicidad propia; no se usa como clave primaria porque no debe condicionar todas las relaciones.
5. Una **clave foránea** exige que el registro relacionado exista. `inscripcion.socio_id` referencia `socio.id` y `actividad_id` referencia `actividad.id`. MySQL rechaza referencias inexistentes.
6. Un socio y una actividad tienen muchas inscripciones. Cada inscripción tiene un socio y una actividad. El vínculo es una entidad porque tiene fecha y estado propios; un `@ManyToMany` simple no expresaría bien esos datos e historial.
7. **JpaRepository** aporta `save`, `findById`, `findAll`, `count` y otros métodos. Spring crea el objeto que implementa nuestras interfaces; no escribimos una clase de implementación.
8. Una **consulta derivada** se interpreta desde el nombre del método: `findByDni` busca el atributo `dni`, y `findByApellidoContainingIgnoreCase` busca una parte del apellido sin distinguir mayúsculas. `findByDisciplina` filtra el catálogo.
9. **@Query** permite expresar una consulta que queremos escribir explícitamente. `InscripcionRepository.contarActivasDeActividad` utiliza JPQL con `Inscripcion`, `actividad.id` y `EstadoInscripcion.ACTIVA`: son nombres Java, no nombres de columnas SQL.
10. **Pageable** indica número de página, tamaño y orden. `PageRequest.of(0, 10, Sort.by("apellido").and(Sort.by("id")))` pide la primera página con un orden estable. `Page` devuelve contenido y cantidad total; el listado de socios utilizará el método heredado `findAll(pageable)` o la búsqueda paginada por apellido.
11. Un **DTO** representará solo los datos necesarios en JSON. Serializar `Socio.inscripciones[*].socio.inscripciones...` podría recorrer un ciclo infinito y además acceder a relaciones diferidas fuera de una transacción. Los futuros servicios convertirán a DTO dentro de la transacción. No se crearon DTOs vacíos ni se afirma haber reproducido un error de serialización.
12. Una operación futura viajará así: controlador MVC/REST → servicio compartido con transacción y reglas → repositorio → Hibernate/JDBC → MySQL → resultado → DTO/vista. Actualmente están implementados el modelo y los repositorios; ese recorrido de negocio aún no está disponible.

## Relaciones reales del código

```mermaid
erDiagram
    SOCIO ||--o{ INSCRIPCION : tiene
    ACTIVIDAD ||--o{ INSCRIPCION : recibe
    SOCIO {
        BIGINT id PK
        VARCHAR dni UK
        VARCHAR nombre
        VARCHAR apellido
        DATE fecha_nacimiento
        VARCHAR email
        VARCHAR telefono
        VARCHAR estado_cuota
    }
    ACTIVIDAD {
        BIGINT id PK
        VARCHAR nombre
        VARCHAR disciplina
        VARCHAR profesor
        VARCHAR dias
        VARCHAR horario
        INT edad_minima
        INT edad_maxima
        INT cupo_maximo
        DECIMAL cuota_mensual
    }
    INSCRIPCION {
        BIGINT id PK
        BIGINT socio_id FK
        BIGINT actividad_id FK
        DATE fecha_inscripcion
        VARCHAR estado
    }
```

`Inscripcion.socio` y `Inscripcion.actividad` son los lados propietarios: sus `@JoinColumn` contienen las claves foráneas. En los otros dos lados, `mappedBy = "socio"` y `mappedBy = "actividad"` señalan atributos Java de Inscripcion. No crean tablas intermedias.

Las colecciones están inicializadas, son inversas y no tienen cascadas de borrado ni `orphanRemoval`. Para persistir un vínculo se asignan ambos padres en Inscripcion y se guarda esta entidad. Si se necesita recorrer ambos lados antes de recargar, el servicio deberá mantener también las listas en memoria; agregar solo a una lista inversa no guarda una inscripción. Los `ManyToOne` son LAZY para no cargar padres innecesariamente.

No existe UNIQUE absoluto sobre socio y actividad. Un vínculo BAJA y otro ACTIVA deben poder coexistir, incluso varias bajas históricas. Tampoco se implementó RN2 mediante un índice condicionado: el servicio futuro deberá decidir la política transaccional y de concurrencia junto con RN1/RN5.

## Tipos e integridad

- `LocalDate` representa fechas sin hora; `DATE` guarda nacimiento e inscripción.
- `BigDecimal` y `DECIMAL(10,2)` conservan importes decimales. Evitar `new BigDecimal(0.1)`; usar `new BigDecimal("0.10")`.
- `horario` es String y VARCHAR(5), compatible con el dato existente `9:00` de Yoga; se admiten H:mm y HH:mm.
- Los estados se guardan por su nombre con `EnumType.STRING`; reordenar las constantes no cambia el significado de lo almacenado. `JdbcTypeCode(VARCHAR)` explicita el mismo tipo del SQL, evitando depender del ENUM nativo del dialecto.
- Bean Validation comprueba campos obligatorios, tamaños, email, DNI, fechas, montos, cupo positivo y orden del rango de edades. No decide si un socio concreto puede inscribirse.
- SQL añade NOT NULL, DNI único, claves foráneas y CHECK de estados, horario y límites numéricos. Las validaciones de email y fechas relativas a hoy permanecen en Java. Hibernate `validate` revisa el mapeo de tablas y columnas, pero no reemplaza las pruebas de índices, CHECK ni datos.
- No se almacena cupoDisponible: será cupoMaximo menos las ACTIVAS consultadas. No se escribieron las reglas ni ese servicio.
- `ddl-auto=validate` no crea ni elimina tablas; el administrador ejecuta el esquema previamente. `spring.sql.init.mode=never` evita cargas automáticas al arrancar.

## Consultas disponibles

```java
// Ejemplo para la futura capa de servicio; todavía no hay un endpoint que lo ejecute.
var pagina = socioRepository.findByApellidoContainingIgnoreCase(
        "pé", PageRequest.of(0, 10, Sort.by("apellido").and(Sort.by("id"))));
var socio = socioRepository.findByDni("30111222"); // Optional<Socio>
long ocupadas = inscripcionRepository.contarActivasDeActividad(actividadId);
boolean duplicada = inscripcionRepository.existsBySocioIdAndActividadIdAndEstado(
        socioId, actividadId, EstadoInscripcion.ACTIVA);
```

`findBySocioIdAndEstado` y `findByActividadIdAndEstado` reciben Pageable; pasar ACTIVA para vigentes, BAJA para historial. `countBySocioIdAndEstado(socioId, ACTIVA)` apoya el futuro máximo de actividades. Estos métodos consultan; no rechazan una inscripción ni garantizan exclusión mutua entre dos peticiones simultáneas.

## Preguntas que pueden hacer

| Pregunta | Respuesta que puede explicar el equipo |
| --- | --- |
| ¿Dónde se valida que no quede cupo? | En el futuro servicio compartido; el repositorio ya cuenta ACTIVAS, pero todavía no implementamos RN1. |
| ¿Por qué no una tabla socio_actividad sin entidad? | Porque la inscripción tiene fecha, estado e historial propios. |
| ¿Qué significa mappedBy? | Indica qué atributo del otro objeto es dueño de la relación y de la clave foránea. |
| ¿Por qué hay un ID y además DNI único? | El ID relaciona registros; el DNI es una restricción del dato del socio. |
| ¿Borrar de la lista elimina una inscripción? | No: la lista es inversa y no tiene borrado en cascada. Las bajas futuras cambiarán el estado y preservarán la fila. |
| ¿Por qué no UNIQUE(socio_id, actividad_id)? | Impediría reinscribirse después de una baja y conservar el historial. |
| ¿existsBy impide duplicados concurrentes? | No. Dos solicitudes pueden consultar antes de guardar. Debemos resolverlo con transacciones y una política de bloqueo en la etapa de servicios. |
| ¿Cómo excluyen las bajas del cupo? | La @Query filtra expresamente EstadoInscripcion.ACTIVA. |
| ¿Pageable comienza en 1? | No: la primera página es 0. Conviene ordenar también por ID para desempatar. |
| ¿Por qué DTO y no devolver la entidad? | Para elegir campos, evitar ciclos y controlar cuándo se accede a relaciones LAZY. |
| ¿Por qué BigDecimal y no double? | Para conservar cantidades decimales monetarias sin aproximaciones binarias. |
| ¿Las pruebas verdes demuestran MySQL? | Solo las pruebas optativas conectadas lo demuestran; integridad en memoria y MVC no prueban persistencia. |
| ¿Qué pasa al ejecutar dos veces seed.sql? | El script agrega solo registros ausentes. No actualiza socios existentes ni reactiva vínculos; su ejecución MySQL queda pendiente de comprobar en este equipo. |
| ¿Por qué el seed tiene más de quince socios? | Yoga necesita catorce personas habilitadas además de los seis escenarios, que incluyen menores y candidatos que deben comenzar sin Yoga. |
| ¿El modelo garantiza todas las reglas? | No. Garantiza integridad básica; RN1–RN5 y la concurrencia se implementarán una vez en servicios. |

Para demostrar resultados, usar [validación de esta etapa](validacion-modelo-datos.md). Para configurar la conexión, seguir [MySQL local](mysql-local.md). Practicar identificando en el código cada anotación y explicando el vínculo entre columna, atributo y consulta.

## Conexión real, scripts y pruebas aisladas

Actualización de preparación: el esquema de desarrollo y ensayo se crea manualmente con una cuenta administradora. La suite ahora desactiva toda inicialización SQL automática y mantiene Hibernate validate. La cuenta socioclub_tests conserva únicamente SELECT/INSERT/UPDATE/DELETE. Antes de cada test se comprueba que la base efectiva sea socioclub_test; los datos de ensayo se revierten con rollback. No se ejecuta seed manualmente en ensayo porque el test de reproducibilidad necesita tablas vacías.

Los scripts compartidos no contienen USE: la base se selecciona en la conexión o temporalmente en el editor de Workbench. El test rechaza USE dentro del seed antes de cargarlo. Esta precaución evita cambiar accidentalmente de ensayo a desarrollo durante una ejecución.

**¿Cómo se conecta Spring Boot a MySQL?** application.properties toma DB_URL, DB_USERNAME y DB_PASSWORD del entorno. La URL JDBC indica servidor, puerto y base, por ejemplo jdbc:mysql://localhost:3306/socioclub. El pool entrega conexiones al driver MySQL Connector/J y Hibernate usa esas conexiones para ejecutar SQL. El servicio Windows debe estar iniciado, la base debe existir y el usuario debe tener permisos. Tener mysql.exe instalado no demuestra que el servidor esté disponible.

**¿Qué hace ddl-auto=validate?** Al arrancar, Hibernate contrasta sus mapeos con las tablas y columnas de la base. Si faltan o tienen tipos incompatibles, el arranque falla. No crea, actualiza ni elimina tablas. No sustituye comprobar claves, índices, CHECK, datos o consultas; por eso hay pruebas reales adicionales.

**¿En qué se diferencian schema.sql y seed.sql?** schema.sql define tablas, columnas, PK, FK, índices y restricciones. seed.sql agrega las ocho actividades y personas/vínculos ficticios de la demostración. Se ejecuta primero schema y después seed sobre desarrollo. El schema no migra tablas antiguas; el seed conserva registros existentes en vez de sobrescribirlos.

**¿Cómo comprobamos que JPA persiste?** Guardamos y hacemos flush, vaciamos el contexto con EntityManager.clear() y recuperamos mediante findById. Así evitamos comprobar solamente el objeto que seguía en memoria. PersistenciaMySqlTests también lee columnas por JDBC, verifica fechas/montos/estados, navega las relaciones y ejecuta consultas de ACTIVAS. Esas comprobaciones solo acreditan MySQL cuando la suite se ejecuta y aprueba contra un servidor real; hasta entonces están preparadas.

**¿Por qué otra base para testing?** Las pruebas crean datos, provocan violaciones de integridad y ensayan bajas y ediciones. socioclub_test separa eso de socioclub. Se exige ese nombre en la URL y se comprueba SELECT DATABASE(); una cuenta restringida únicamente a ensayo refuerza el aislamiento. Los datos de cada test se revierten con rollback. Las tablas DDL y el avance de AUTO_INCREMENT pueden permanecer: no se debe usar una base con datos personales o trabajo de desarrollo.

**¿Una conexión fallida aparece como prueba omitida?** Con MYSQL_TEST_DB_URL configurada, la suite se activa aunque falte MYSQL_TEST_ENABLED. Una conexión rechazada produce errores y Maven falla. Sin configuración de ensayo, los tests se omiten con una razón expresa. En la revisión se ejecutaron controles negativos para comprobar esa diferencia; ninguno representa persistencia aprobada.

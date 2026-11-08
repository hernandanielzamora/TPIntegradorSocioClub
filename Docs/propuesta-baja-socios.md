# Propuesta original de baja lógica de socios — registro histórico

Actualización: el usuario aprobó diseño e implementación y preparación de la migración, pero prohibió ejecutarla. El código y script ya preparados, resultados y pasos controlados se documentan en [baja-logica-socios.md](baja-logica-socios.md). Las alternativas y estados pendientes descritos debajo corresponden al análisis previo a esa aprobación; no sustituyen el estado actual.

Fecha: 2026-11-08. Rama: feature/backend-api-rest. No hay implementación ni migración aplicada de esta mejora. El usuario propuso personalmente la funcionalidad al revisar la API, tras advertir que existía baja de inscripciones pero no de socios. El análisis y esta propuesta son de Codex; no se atribuyen al usuario ediciones de código.

## Alcance y motivo para detenerse

La consigna original, página 3, RF5, exige alta/modificación/listado de socios, no su baja. RF9 se refiere a baja de una actividad mediante estado BAJA de la inscripción. El Anexo A, página 13, no incluye baja del socio. Se registra como mejora funcional voluntaria propuesta por el usuario, no como requisito obligatorio ni corrección de una regla incumplida.

Socio y schema.sql solo tienen estado_cuota (AL_DIA/MOROSO). No existe atributo de vigencia. No reutilizar cuota, email, DNI o ausencia de inscripciones para inferir INACTIVO. Por instrucción expresa del usuario se detiene la implementación dependiente hasta aprobar el cambio de esquema. No se modifica entidad, enum, schema, seed, servicios, controllers o colección con una ruta todavía inexistente.

## Diseño recomendado

- Nuevo enum EstadoSocio: ACTIVO/INACTIVO, independiente de EstadoCuota.
- Nueva columna socio.estado, VARCHAR(8) NOT NULL DEFAULT 'ACTIVO', CHECK propio ck_socio_vigencia. No reutilizar ck_socio_estado: ya restringe estado_cuota.
- Mapear el enum como VARCHAR, igual que los enums existentes del proyecto. Inicializar nuevos socios ACTIVO en Java; el default SQL protege cargas que omiten el campo.
- No borrar socios ni inscripciones. No crear reactivación, fecha/motivo de baja o restricciones nuevas de identidad en esta propuesta.
- Al inactivar, recomendar dar de baja todas sus inscripciones ACTIVAS en la misma transacción para liberar cupos. Mantener fecha/vínculos y todas las BAJAS previas; no tocar cuotas o pagos.
- Nueva inscripción de INACTIVO: 409 con error público y código estable SOCIO_INACTIVO. No etiquetarlo falsamente RN1–RN5; ampliar el contrato con código (regla null para esta mejora) y conservar error/regla/detalles compatibles.
- Repetir la baja: 200 con el socio INACTIVO, sin duplicar cambios o alterar historial. Si se detectan ACTIVAS de un INACTIVO por una operación externa, normalizarlas a BAJA dentro de la misma operación; la idempotencia es del efecto final.

### Endpoint y MVC propuestos

PUT /api/socios/{id}/baja: 200 SocioResponse con nuevo campo estado; 404 si no existe, 400 ID inválido. PUT se prefiere aquí por operación idempotente de transición y consistencia con PUT /api/inscripciones/{id}/baja ya exigido. PATCH sería una alternativa razonable para modificar parcialmente una representación, pero no es necesario añadir otro formato ni dos rutas para esta misma operación.

MVC: GET de una pantalla de confirmación sin mutaciones, indicando cuántas ACTIVAS se darán de baja; POST para confirmar; servicio compartido con REST; PRG con mensaje. Mostrar estado en listado/edición, sin permitir reactivar incidentalmente desde el formulario general. Conservar búsqueda y consulta del historial. Filtrar INACTIVOS del selector de nuevas inscripciones como ayuda de interfaz, y siempre validar en servicio ante requests directos.

El socio INACTIVO sigue siendo consultable por DNI e ID a través de rutas ya existentes, con estado explícito; GET de ACTIVAS devolverá [] después de la baja y el historial BAJA continuará en Secretaría. No simular un login ni autenticación por DNI. No modificar React ahora; posteriormente deberá mostrar el estado y tratar el 409 nuevo.

## Transacción y concurrencia

Servicio de baja del socio con READ_COMMITTED y bloqueo PESSIMISTIC_WRITE del socio primero. Después obtener las actividades afectadas por sus ACTIVAS y bloquearlas en orden creciente de ID, sin adquirir nuevos socios desde un bloqueo de actividad. Releer/actualizar las inscripciones pertinentes a BAJA y estado del socio a INACTIVO; confirmar todo junto. Ante error, rollback de ambos cambios. No ejecutar DDL ni commits intermedios en esta transacción de negocio.

El alta existente ya bloquea socio→actividad. Si el alta confirma primero, la baja debe incluir ese vínculo; si confirma primero la baja, el alta posterior ve INACTIVO y se rechaza. Dos bajas del mismo socio se serializan. El orden creciente de múltiples actividades reduce ciclos entre bajas de socios distintos; no se promete ausencia total de deadlocks. Las futuras operaciones deben respetar el mismo protocolo. Evitar actualizaciones masivas JPQL que dejen entidades gestionadas desactualizadas, salvo que se controle explícitamente flush/clear.

## Migración propuesta, aún no ejecutable por la aplicación

Antes: respaldo verificado, sin escritores concurrentes, revisar columnas/constraints y contar las tres tablas de cada base con la cuenta administradora. Las cuentas socioclub_app/tests tienen solo CRUD y no deben recibir ALTER. Seleccionar y comprobar cada catálogo por separado; detenerse ante cualquier estado distinto del esperado. No se leyó .env ni se hicieron estas consultas autenticadas por Codex.

SQL exacto propuesto para una base que todavía no tiene la columna, a ejecutar una única vez tras aprobación y revisión del catálogo:

```sql
SELECT DATABASE();
ALTER TABLE socio
    ADD COLUMN estado VARCHAR(8) NOT NULL DEFAULT 'ACTIVO',
    ADD CONSTRAINT ck_socio_vigencia CHECK (estado IN ('ACTIVO', 'INACTIVO'));
```

No es un script aplicado ni se incorpora USE en los scripts compartidos. El default hace que los registros existentes queden ACTIVO sin borrar filas ni cambiar IDs, cuota, datos personales o relaciones. No hay DROP, DELETE, TRUNCATE o reinicialización. No ejecutar de nuevo si la columna/constraint ya existe: inspeccionar y verificar su definición.

MySQL DDL implica commits implícitos: no prometer revertir ALTER con ROLLBACK de una transacción de aplicación. Ante fallo, revisar el estado real antes de reintentar; conservar backup y código anterior. Una reversión posterior se planifica por separado y no elimina automáticamente una columna con decisiones de baja ya registradas.

Orden recomendado: aprobar diseño y migración; preparar copia/esquema de ensayo y migrar socioclub_test primero; implementar entidad/servicios/API/MVC y pruebas; verificar datos/constraints y suite de ensayo; con aprobación del operador aplicar la misma migración conservadora a socioclub antes de arrancar el código nuevo. ddl-auto=validate permanecerá activo: el código nuevo no arrancará contra una base aún no migrada. No hay migraciones automáticas de inicio ni ampliación de permisos de la aplicación.

Para instalaciones nuevas, schema.sql incluiría la columna/default/check tras aprobarse. CREATE TABLE IF NOT EXISTS no migra tablas existentes. seed.sql puede seguir omitiendo estado y usar el default para registros nuevos, pero debe verificarse y probarse que repetir el seed nunca reactiva un INACTIVO. Los registros existentes deben conservar su estado.

## Impacto por componente

| Componente | Cambio requerido después de aprobar |
| --- | --- |
| MySQL desarrollo y testing | Misma migración aditiva en ambas bases, sin borrar datos; comprobar que no cambian conteos/PK/FK/cuotas |
| Entidad y constructor | EstadoSocio, columna y valor inicial ACTIVO; validar persistencia VARCHAR |
| Servicio de socios | Baja transaccional compartida y protección contra reactivación por edición general |
| InscripcionService | Rechazo adicional de INACTIVO antes de RN1–RN5, manteniendo sus códigos/semántica |
| Consultas y DTO | Nuevo estado independiente de estadoCuota; historial accesible y opciones de alta solo activos |
| API | Décimo endpoint voluntario, 409 SOCIO_INACTIVO y actualización documentada del error/response |
| MVC | Confirmación GET sin mutar, POST de baja, estado visible, mensajes y PRG |
| Seed | Defaults en altas nuevas; no cambiar INACTIVOS existentes al repetir carga |
| Tests actuales | Nuevos socios activos por defecto; adaptar DTOs/fixtures sin relajar pruebas existentes |
| React | Ningún archivo modificado ahora; API agrega campo compatible, pero integrar manejo de INACTIVO quedará pendiente |
| Postman | Agregar la décima ruta y casos solo cuando exista; no anunciarla como disponible ahora |

## Plan de pruebas después de aprobar

Unitarias: baja sin ACTIVAS, con varias ACTIVAS, socio inexistente, repetición, MOROSO, estadoCuota intacto, historial/fecha/vínculos conservados y alta rechazada por INACTIVO. MVC: confirmación, POST/PRG, ausencia de acción por GET y mensajes. REST: 200/400/404, error de INACTIVO con código correcto y contrato compatible para los nueve endpoints previos.

Integración solo socioclub_test: estado/default/check, persistencia de baja múltiple, liberación de todos los cupos, rollback real ante fallo durante la operación, repetición de seed sin reactivación, dos bajas simultáneas y carrera entre alta y baja. Fixtures propias y limpieza controlada, sin borrar tablas preexistentes ni conectar tests a socioclub. Fallo de baja debe dejar socio e inscripciones sin cambios.

Ninguna de estas pruebas nuevas está implementada o ejecutada en esta propuesta.

## Auditoría de la API actual

Se revisaron nueve rutas, DTOs, advice acotado, reutilización de servicios y CORS. SocioResponse omite contacto; actividad anidada no incluye relaciones inversas. RN1–RN5 continúan en InscripcionService; errores de negocio 409 conservan error/regla/detalles para React. Advice JSON no sustituye los mensajes HTML de MVC. No se detectó un impedimento nuevo en el contrato actual para uso local.

El usuario confirmó manualmente que GET /api/actividades devuelve JSON. Los nueve XML disponibles ahora muestran 109 aprobadas (30 REST, 7 fachada, 23 MVC Secretaría, 1 raíz, 10 administración, 19 inscripción, 5 integridad, 7 persistencia y 7 integración), 0 fallos/errores/omisiones. Es evidencia de ejecución disponible, no una ejecución autenticada nueva de Codex ni del goal verify completo inferido solo del XML. El verify propio anterior fue BUILD SUCCESS con 95 aprobadas/14 omitidas. No se mezclan resultados ni se atribuye una revisión de navegador completa no comunicada.

CORS sigue restringido a los orígenes locales configurados; sin autenticación/autorización o protección CSRF específica. No listo para exposición pública. La colección actual conserva los nueve endpoints y 18 requests; JSON válido y ejecución de runner no confirmada. No se agrega una request de baja del socio todavía.

## Decisiones a aprobar

1. EstadoSocio separado con columna estado/default/check y migración conservadora en ambas bases.
2. Baja automática de todas las ACTIVAS, atómica con INACTIVO; reintentos idempotentes 200.
3. PUT de baja y POST con confirmación MVC, consultas históricas disponibles, sin reactivación en esta etapa.
4. 409 con código SOCIO_INACTIVO como mejora independiente de RN1–RN5.

La aprobación del diseño no equivale a que una migración ya esté ejecutada. Debe quedar explícito quién la ejecutará y en qué base antes de aplicarla. Hasta entonces no está lista esta mejora para integrar a dev. No hay commit, push o merge.

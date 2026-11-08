# Prompt 21 — FE-ADMIN-01 y RN8: auditoría y propuesta para aprobación

> Decisión vigente (Prompt 22): FE-ADMIN-01 y RN8 POSTERGADAS, NO IMPLEMENTADAS. No solicitar su ejecución para cerrar el proyecto. SQL conservado en database/propuestas; ver [mejoras-futuras.md](mejoras-futuras.md). El análisis siguiente queda como antecedente de diseño.

Estado: preparación no ejecutada. Rama feature/react-integracion-api; árbol limpio al comenzar, HEAD 35e5547. FE-ADMIN-01 y RN8 son ampliaciones propuestas por el usuario al evaluar el sistema funcionando, no sustituyen RF1–RF10/RN1–RN7. No se alteraron bases ni se implementó una pantalla que simule protección.

## Alcance revisado y evidencia disponible

Se revisaron Actividad, Socio/EstadoSocio, Inscripcion y sus FK, schema.sql, migración 001, seed, InscripcionService/ActividadService, Secretaría MVC, ApiController/DTOs, WebConfig, pom, Router/Axios y documentación/tests existentes. El PDF original está en Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf; se extrajeron literales mediante Python/zlib sin dependencias porque no hay lector PDF instalado. Es extracción parcial (no valida diagramas o texto codificado en otras fuentes). Los apartados RF/RN y Anexo A extraídos coinciden con los requisitos originales ya trazados; no se observó hora final/duración en el catálogo inicial. No se presenta la lectura parcial como auditoría visual íntegra del PDF.

No se consultó MySQL ni se abrió .env. Por ello solo se conocen los formatos de los scripts y el ejemplo humano de Funcional Jóvenes; NO el inventario vivo completo. No se confirma que exista esa actividad adicional en cada base. Antes de backfill el usuario debe ejecutar el inventario de solo lectura y entregar resultados sin datos personales.

## Horarios: hallazgos reales

actividad.dias es VARCHAR(50) / String, NotBlank/Size; no parser, enumeración, validación de nombres de días ni normalización. Actividad.horario es VARCHAR(5) / String con regex H:mm o HH:mm y CHECK SQL equivalente; se valida el reloj, no una duración. El servicio guarda ambos valores sin transformar. No existen horaFin, LocalTime, DayOfWeek, ActividadHorario ni exclusión de conflictos. RN1–RN5 comprueban cuota, edad, duplicado, máximo y cupo; no comparan horarios.

| Actividad en seed | Texto de días | Inicio | Conversión candidata (ISO 1=lunes) | Fin |
| --- | --- | --- | --- | --- |
| Fútbol infantil | Lun y Mié | 18:00 | 1,3 | No informado |
| Vóley mixto | Mar y Jue | 19:00 | 2,4 | No informado |
| Básquet juvenil | Lun y Vie | 19:30 | 1,5 | No informado |
| Natación | Mar y Jue | 17:00 | 2,4 | No informado |
| Patín artístico | Mié y Vie | 17:30 | 3,5 | No informado |
| Ajedrez | Sáb | 10:00 | 6 | No informado |
| Funcional adultos | Lun, Mié y Vie | 20:30 | 1,3,5 | No informado |
| Yoga | Mar y Jue | 9:00 | 2,4 | No informado |

El fixture manual agrega Lun y Mie / Mar y Jue, 10:00/11:00. El ejemplo humano Lun, Mié y Vie / Lunes, Miércoles y Viernes tiene días equivalentes y comienzo 20:30; no permite deducir la finalización. La conversión de la tabla es propuesta, no datos aplicados. Todos los registros sin fin requieren revisión: NO asumir 60 minutos, ni tratar igualdad de inicio como solución suficiente a solapamientos parciales.

Inventario manual de solo lectura, desde una conexión seleccionada expresamente en el catálogo a revisar:

```sql
SELECT DATABASE(), @@hostname, @@port, VERSION();
SELECT id,nombre,dias,horario FROM actividad ORDER BY id;
SELECT dias,horario,COUNT(*) AS cantidad FROM actividad GROUP BY dias,horario ORDER BY dias,horario;
SELECT id,nombre,dias,horario,'REQUIERE_HORA_FIN_Y_REVISION_DE_DIAS' AS pendiente
FROM actividad ORDER BY id;
```

No ejecutar seed otra vez para normalizar. Registrar ID, texto original, días reconocidos, hora inicio, fin aprobado por cada día, estado y motivo de cualquier rechazo en un reporte fuera de Git si contiene datos reales. Mantener los textos originales como evidencia.

## Seguridad administrativa actual

No hay Spring Security, SecurityFilterChain, PasswordEncoder, usuarios administrativos, sesiones admin ni CSRF. Socio tiene DNI y estado de cuota/vigencia, no contraseña/rol. React RutaProtegida solo verifica identificación en contexto y sessionStorage conserva DNI; eso no autoriza a administrar. No existen /admin ni login admin. No es seguro dar privilegios por un DNI o por estadoCuota.

Secretaría ya tiene dashboard, ABM de socios/actividades, listado de inscripciones, filtros, cupos, alta/baja y baja lógica de socio. Todo /secretaria es accesible sin sesión actualmente, incluidos POST de cambios. REST permite sin autenticación POST/PUT/DELETE de actividades, POST inscripción, PUT baja de inscripción y PUT baja de socio. WebConfig limita orígenes de /api/**, pero no autentica, no exige CSRF, no habilita credenciales de sesión ni el header CSRF. Hay DTOs sin ciclos; tampoco hay protección contra consultar datos personales con DNI ajeno. CORS no reemplaza autorización.

## Diseño propuesto FE-ADMIN-01 (pendiente de implementar)

Agregar Spring Security con versión administrada por Spring Boot 4.1.2 y su soporte de tests, sin fijar manualmente versiones incompatibles. No se cambió pom ni se descargaron esas dependencias en esta preparación. Cuenta administrativa separada con DNI único, hash adaptativo (propuesta BCrypt mediante DelegatingPasswordEncoder, coste calibrado), rol ADMIN y habilitada=false por defecto. No vincularla obligatoriamente a socio/deuda y no provisionar ninguna cuenta/clave pública. DNI, contraseña y habilitación inicial se definirán por canal local autorizado después de revisar el esquema, nunca en seed, .env.example, React o commits. No solicitar contraseñas por chat.

Sesión servidor JSESSIONID, HttpOnly, SameSite=Lax en local y Secure con HTTPS en despliegue; rotación de sesión al autenticar y expiración/logout reales. React consulta sesión/rol en servidor, no almacena contraseña ni isAdmin. Login con DNI/contraseña comprobados por AuthenticationManager y PasswordEncoder, mensajes uniformes para credenciales inválidas y limitación de intentos. El PIN corto no se recomienda sin controles adicionales; esta propuesta usa contraseña. Login/CSRF/logout son públicos en autorización pero no se omite CSRF en sus POST. No construir un login que solo guarde un SecurityContext en memoria sin persistirlo en la sesión.

Endpoints propuestos (NO existen aún): GET /api/auth/csrf, POST /api/auth/login (formulario DNI/contraseña adaptado al filtro Spring), GET /api/auth/sesion y POST /api/auth/logout. CsrfToken almacenado en sesión y token expuesto por GET, reenviado en header X-CSRF-TOKEN desde Axios. Obtener otro token tras login/logout, porque Spring limpia el anterior. Usar soporte SPA conforme a la versión de Spring Security, no desactivar CSRF. Formularios Thymeleaf también deben enviar el token.

API administrativa /api/admin/** y TODO /secretaria/** requieren ADMIN (incluidas consultas personales). Proteger también los endpoints administrativos existentes POST/PUT/DELETE /api/actividades y PUT /api/socios/{id}/baja: no dejar rutas alternativas abiertas. Las cuatro rutas React de socios y las lecturas públicas del Anexo A permanecen. POST/PUT de inscripción académica siguen permitidos sin rol ADMIN, con CSRF, sin presentarlos como autenticación segura de socios: persiste la limitación de que el servidor no comprueba identidad del DNI. No declarar seguridad integral por proteger solo administración.

401 para API sin sesión cuando corresponda, 403 por rol insuficiente o token CSRF inválido; con CSRF ausente el filtro puede rechazar antes de autenticación, por lo que no prometer 401 para todos los POST anónimos. MVC debe redirigir al login conservando su funcionamiento y usar CSRF en sus POST. Logout invalida la sesión y no es una operación GET.

Cliente Axios administrativo con withCredentials y token CSRF en memoria; actualizar también las escrituras públicas de socios para obtener/reenviar CSRF. CORS con orígenes exactos, allowCredentials=true y headers Content-Type/Accept/X-CSRF-TOKEN, nunca '*'. Para cookies usar el MISMO hostname en React y API: localhost:3000 + localhost:8080; en ensayo 127.0.0.1:3000 + 127.0.0.1:8081 requiere autorizar explícitamente el origen de ensayo. Mezclar localhost con 127.0.0.1 puede impedir cookies SameSite y no se arregla solo con CORS.

React /admin/login y /admin con dashboard, socios buscables/paginados, cuota/vigencia, alta/edición/baja confirmada, ABM de actividades y listado de ACTIVAS por actividad. Reutilizar SocioService/ActividadService/ConsultaSecretariaService/InscripcionService; nuevos DTOs/controller administrativos, sin repositorios desde controller. Endpoints propuestos GET /api/admin/resumen; GET/POST /api/admin/socios; GET/PUT /api/admin/socios/{id}; PUT /api/admin/socios/{id}/baja; GET /api/admin/actividades/{id}/inscriptos. Actividades pueden reutilizar los endpoints existentes ahora protegidos. Ningún hash se serializa. La guarda de ruta React solo mejora navegación: el servidor debe rechazar acceso directo/API sin rol.

Referencias: [CSRF y SPA](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html), [almacenamiento de contraseñas](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html). Son fundamentos del diseño, no una configuración de seguridad ya implementada.

## Modelo y algoritmo propuestos RN8

ActividadHorario asociado a Actividad, con dia_semana ISO 1–7 (DayOfWeek.getValue; converter explícito, NO EnumType.ORDINAL) e inicio/fin LocalTime ↔ MySQL TIME. Intervalos en el mismo día: 00:00 <= inicio < fin < 24:00. No aceptar cruces de medianoche silenciosamente; quedan fuera de la primera versión y requieren diseño explícito. Una actividad tiene muchos intervalos. No hay DELETE CASCADE. Una UNIQUE impide duplicado exacto, pero no basta para impedir intervalos superpuestos de una misma actividad: validarlo en el servicio.

Campo actividad.horarios_confirmados=false para distinguir completitud de una carga parcial; solo true tras validación humana de TODOS los días/intervalos y coherencia con el texto original. No basta con encontrar una fila para declarar el horario completo. El texto dias/horario se conserva para compatibilidad; el DTO agrega horarios [{diaSemana,horaInicio,horaFin}] y horariosConfirmados. La presentación canónica se genera del modelo estructurado, sin dos fuentes independientes editables. Legacy POST/PUT con solo dias/horario no permite inferir fin: puede mantener actividad pendiente, pero no habilitar nuevas inscripciones hasta completar horario. Documentar ese cambio, adaptar Postman y fixtures/tests originales antes de activar RN8.

Normalización futura: trim, minúsculas, quitar tildes solo al reconocer tokens; separar comas y conjunción aislada 'y'; mapear lun/lunes, mar/martes, mie/miércoles/miercoles, jue/jueves, vie/viernes, sab/sábado/sabado, dom/domingo. Duplicados, vacíos, guiones/rangos no soportados y tokens desconocidos se reportan, sin ignorarlos ni convertirlos por substrings. Parseo de inicio admite exactamente H:mm/HH:mm con reloj válido y lo representa HH:mm. Fin se obtiene de decisión humana por actividad/día, no del parser. Reportar horarios inválidos y conflictos históricos sin cambiar inscripciones.

Dentro de InscripcionService, conservar orden/prioridad RN1–RN5 y vigencia: bloquear socio; obtener IDs de actividades con inscripciones ACTIVAS + destino; bloquear actividades DISTINTAS en orden creciente antes de leer horarios; validar completitud de destino y todas las activas; comprobar día igual y inicioA < finB && finA > inicioB. Consecutivas se permiten. BAJA no participa. Si faltan datos: 409 codigo HORARIO_PENDIENTE, regla null; no afirmar CONFLICTO_HORARIO sin intervalos conocidos. Conflicto comprobado: 409 codigo CONFLICTO_HORARIO, regla RN8; mensaje claro y opcional actividad en conflicto (ID/nombre, sin datos de otra persona).

El bloqueo del socio serializa altas concurrentes de la misma persona. Cambiar solo el alta NO garantiza seguridad si el administrador puede editar horarios simultáneamente. Propuesta inicial conservadora: impedir modificaciones de horario/días de actividades con ACTIVAS (409) y usar el bloqueo de actividad en toda edición; permitir cambios no temporales que no alteren el horario. Revalidar bajo lock y no adquirir locks en orden inverso. Una futura edición horaria con inscriptos requiere otro protocolo y pruebas; no prometerla aquí. Altas a actividades distintas se bloquean en orden global; respetar también bajas de socio/inscripción y no introducir un lock de socio después de actividad. No se promete ausencia de deadlocks frente a SQL ajeno al protocolo.

React mostrará rangos normalizados y duración calculada solo con intervalos completos; los pendientes dirán 'horario pendiente', no una duración supuesta. Preaviso de conflicto basado en ACTIVAS es informativo y se actualiza al cambiar inscripciones; backend es autoridad y puede devolver 409 aun si el cliente veía disponibilidad. Mantener RN6, baja, loading y errores. Todo esto está propuesto, NO conectado en el código actual.

## Migraciones preparadas y aprobación requerida

[002_cuenta_administrativa.sql](../database/propuestas/002_cuenta_administrativa.sql): tabla nueva vacía, sin cuenta/clave de ejemplo. [003_actividad_horario.sql](../database/propuestas/003_actividad_horario.sql): tabla nueva vacía y bandera horarios_confirmados=false, sin backfill o actualización de las inscripciones. Requieren declarar explícitamente catálogo esperado y autorización en esa conexión; de lo contrario solo informan NO APLICADA. No son migraciones ejecutadas ni probadas en MySQL. No se modifican schema.sql/seed.sql todavía para no presentar un modelo definitivo aprobado.

Antes de autorizar: revisar inventario vivo, definir fin de cada intervalo, revisar diseño/columnas y acordar provisión privada de la primera cuenta. Detener escritores, respaldar estructura/datos de ambas bases fuera de Git y verificar recuperación aislada; no se afirma que se haya hecho ahora. Ensayar primero en socioclub_test sin borrar sus fixtures; las suites MySQL que exigen vacío no pueden correrse contra esos datos. DDL tiene commits implícitos, no hay rollback mediante START TRANSACTION. 003 tiene DOS DDL: si falla el segundo puede quedar estado parcial. Detener ante errores; no ignorarlos. Segunda ejecución omite cambios si detecta objetos, no repara parciales: inspeccionar y no asumir éxito. No ejecutar los dos catálogos automáticamente.

Reversibilidad por recuperación documentada: respaldo del estado previo y restauración autorizada primero en entorno aislado, luego plan coordinado si se necesitara recuperar. No hay down destructivo automático: restaurar el respaldo previo después de nuevas operaciones perdería datos nuevos, de modo que requiere conciliar/capturar cambios y otra autorización. No dar permisos ALTER al usuario app/tests ni usar root en Spring.

## Puerta de continuación, pruebas y autoría

El propio pedido del usuario exige «si la integración requiere alterar la base de datos, preparar el SQL y detenerte para aprobación manual». Este requisito aplica a ambas mejoras. Se entregan auditoría, diseño y DDL revisables; NO se agregaron entidades/controladores/pantallas parciales ni dependencias que cambien el backend en ejecución antes de esa aprobación. No hay FE-ADMIN-01/RN8 implementadas ni autenticación/normalización nuevas disponibles. El siguiente paso es aprobar el esquema/diseño y completar datos, no ejecutar automáticamente la migración.

Pruebas nuevas pendientes tras aprobación: login válido/inválido, cookie/sesión/rol, usuario anónimo y rol USER, acceso directo/alternativo, logout, CSRF válido/ausente/incorrecto y renovación tras login/logout, CORS con credenciales; RN8 igualdad/parcial/consecutivas/días distintos/varios días/BAJA/normalización/formatos inválidos/horarios incompletos/concurrencia/editor; API 409; React mensajes, login y guardas. Mantener las 111 sin MySQL, 17 MySQL preparadas y 58 React previas. No convertir resultados anteriores en evidencia de estas funciones nuevas. Prueba real MySQL nueva solo tras migración aprobada y entorno adecuado, sin limpiar testing.

Propuesta funcional humana y ejemplo de Funcional Jóvenes: usuario. Auditoría, diseño y archivos SQL/documentos: Codex. No consta que el usuario haya escrito el SQL propuesto ni editado Java/React en esta etapa. Mantener AH-1–AH-3 e IH-MIG-01 con sus límites académicos. No se hizo commit, push, merge ni cambios en main/dev/sitio-publico. Las comprobaciones anteriores de funcionamiento general no validan las mejoras EXTRA.

### Reporte histórico propuesto, solo DESPUÉS de migración y carga de horarios aprobadas

Este SELECT no modifica inscripciones. No ejecutarlo hoy si actividad_horario todavía no existe. Solo demuestra conflictos entre intervalos ya cargados; los registros sin horario confirmado deben informarse por separado y no considerarse libres de conflicto.

```sql
SELECT id,nombre,dias,horario FROM actividad WHERE horarios_confirmados=FALSE ORDER BY id;
SELECT i1.socio_id,i1.id AS inscripcion_1,i2.id AS inscripcion_2,
       h1.actividad_id AS actividad_1,h2.actividad_id AS actividad_2,h1.dia_semana,
       h1.hora_inicio AS inicio_1,h1.hora_fin AS fin_1,
       h2.hora_inicio AS inicio_2,h2.hora_fin AS fin_2
FROM inscripcion i1
JOIN inscripcion i2 ON i2.socio_id=i1.socio_id AND i1.id<i2.id
JOIN actividad_horario h1 ON h1.actividad_id=i1.actividad_id
JOIN actividad_horario h2 ON h2.actividad_id=i2.actividad_id AND h2.dia_semana=h1.dia_semana
WHERE i1.estado='ACTIVA' AND i2.estado='ACTIVA'
  AND h1.hora_inicio<h2.hora_fin AND h1.hora_fin>h2.hora_inicio
ORDER BY i1.socio_id,i1.id,i2.id,h1.dia_semana;
```

Verificación efectiva de esta preparación: npm.cmd test -- --watchAll=false --runInBand ejecutado por Codex: cinco suites, 58 aprobadas, 0 fallidas y 0 omitidas (regresión EXISTENTE, no pruebas de FE-ADMIN-01/RN8). No se ejecutó Maven/MySQL ni build nuevo porque no cambió código de aplicación. git diff --check satisfactorio; .env sigue ignorado y no se leyó. El BOM local oficial spring-boot-dependencies 4.1.2 administra Spring Security 7.1.2; se contrastó su documentación, sin agregar la dependencia todavía. Git: seis documentos modificados y tres archivos nuevos (propuesta y dos SQL); sin commit/push/merge.

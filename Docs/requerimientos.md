# Requerimientos y trazabilidad — SocioClub

## Matriz vigente de cierre — Prompt 22

Referencias originales: enunciado secciones 3.1/3.2 y Parte A–E. Estados describen el alcance y la evidencia, no acreditan recorridos humanos no realizados. Las matrices anteriores se conservan abajo como historial de etapas.

| ID | Implementación y archivos / módulos | Evidencia actual | Estado |
| --- | --- | --- | --- |
| RF1 | Sitio: ocho Actividad y tarjetas DOM (script.js); backend y React catálogo real | 25 sitio, GET real catálogo/detalle 200, tests React | Implementado y probado |
| RF2 | Sitio: filtros combinados disciplina/edad; React disciplina | Sitio bordes/combinación/vacío/edad inválida; React filtro | Implementado y probado |
| RF3 | Preinscripción: validación local accesible, sin recepción remota/MySQL | 25 sitio incluyen formulario válido/inválido y preventDefault; alcance en sitio-publico.md | Implementado y probado |
| RF4 | ActividadService, Secretaría, nueve endpoints API para ABM y cupos | Tests servicio/MockMvc 200/201/204/400/404/409; GET real 200 | Implementado y probado |
| RF5 | SocioService/repositorio, formulario/listado/búsqueda/paginación MVC | Tests servicio/MVC; Secretaría real 200 | Implementado y probado |
| RF6 | InscripcionService compartido por MVC/API; React POST | Unitarios RN1–RN5 y Axios; flujo de escritura navegador pendiente | Implementado y probado |
| RF7 | ConsultaSecretariaService, listado inscriptos y cupo calculado de ACTIVAS | Unitarios/MVC/DTO; catálogo real 200; cupos finales manuales pendientes | Implementado y probado |
| RF8 | DNI por API, contexto mínimo, ACTIVAS por socio | React/contrato; consulta real de socio y ACTIVAS 200 | Implementado y probado |
| RF9 | PUT baja de inscripción, confirmación React y POST MVC; historial BAJA | Unitarios/MockMvc/RTL; flujo integral manual pendiente | Implementado y probado |
| RF10 | ResumenCuota y calcularCuotaMensual, precios del DTO real | Jest cero/una/dos/tres y centavos; BAJA excluida | Implementado y probado |
| RN1 | InscripcionService cupo, lock de actividad | aceptaUltimoCupo/rechazaCupoCompletoOYaExcedido; API RN1; React conflicto | Implementado y probado |
| RN2 | Duplicado ACTIVA; BAJA permite reinscribir sin reactivar | rechazaActivaDuplicada/reinscripcionNoReactivaNiModificaLaBaja; API RN2 | Implementado y probado |
| RN3 | Period/Clock, edad mínima/máxima inclusivas | Cumpleaños y bisiesto, API RN3, filtro sitio inclusivo | Implementado y probado |
| RN4 | MOROSO rechaza alta, permite consulta/baja | rechazaSocioMoroso; servicios/contrato; API RN4 | Implementado y probado |
| RN5 | Máximo tres ACTIVAS, lock de socio | aceptaTerceraActividadContandoSoloActivas/rechazaCuartaActividadOConteoYaExcedido; API RN5 | Implementado y probado |
| RN6 | Función JS con bonificación 0/10/15%, centavos | Jest 0/1/2/3 actividades y redondeo | Implementado y probado |
| RN7 | Validación DNI sitio/React y Bean Validation SQL | Tests sitio/React 7/8 válidos; puntos/letras/longitudes inválidos | Implementado y probado |
| FE-ADMIN-01 / RN8 | Solo diseño preservado en propuestas | NO ejecutados, sin dependencias/función parcial | Mejora futura fuera del alcance |

“Implementado y probado” aquí acredita pruebas automatizadas descritas y consultas explícitas, no los seis casos E2E manuales. Ver [pruebas-sistema.md](pruebas-sistema.md) y [cierre-desarrollo.md](cierre-desarrollo.md). No representa garantía de seguridad pública ni nueva corrida MySQL. RF3 tiene el límite expreso de recepción local: no afirmar recepción remota o almacenamiento. Si la interpretación docente exige esa recepción, requiere resolver el alcance antes de entregar. La tabla no reemplaza ese aviso.

## Antecedentes de matrices anteriores

Actualización de etapa RN1–RN5 (2026-11-08): la persistencia anterior fue validada e integrada por PR #3. Ahora InscripcionService implementa las reglas con bloqueos pesimistas y transacción READ_COMMITTED. Verify final: 36 aprobadas, 0 fallos/errores/omisiones, incluidas las 19 unitarias del servicio y 11 MySQL. Ejecución autenticada confirmada por el usuario y XML inspeccionados por Codex. Ver [detalle](reglas-negocio.md). Los estados históricos siguientes describen la preparación anterior; RF6 no se considera integralmente completo sin controladores e integración.

Fuente principal: [TPIntegradorSocioClubEnunciado.pdf](Enunciado/TPIntegradorSocioClubEnunciado.pdf), leído íntegramente (14 páginas) el 2026-11-02.

Estado actualizado al 2026-11-08: PR #1 (sitio público) y #2 (React) integrados y preservados. Modelo JPA, repositorios, esquema y seed implementados. El usuario confirmó MySQL operativo y preparación manual de bases, cuentas, tablas y datos; la persistencia real JPA sigue pendiente porque las credenciales no están disponibles para la ejecución de Codex. Maven: 6 aprobadas/7 omitidas; React: build exitoso y 20 aprobadas. Los RF que dependen del backend continúan sin cumplimiento integral; no hay integración con API ni pruebas de sistema ejecutadas.

Esta etapa prepara RF4–RF9: tres entidades, tres JpaRepository, búsqueda por DNI/apellido, Pageable, filtro por disciplina, consultas por estado y conteos. El seed incluye ocho actividades exactas, 21 socios sintéticos, dos MOROSOS y Yoga con 14/15 plazas. Ver [validación](validacion-modelo-datos.md), [datos](datos-modelo.md) y [guía de defensa](guia-defensa-tecnica.md). En la matriz, Pendiente significa cumplimiento integral pendiente.

Componentes: A sitio público; B secretaría Thymeleaf; C API REST; D app React; E pruebas; F documentación.

## Matriz funcional — sección 3.1, página 3

| Código | Requisito o regla | Componente responsable | Implementación pendiente | Prueba prevista | Estado |
| --- | --- | --- | --- | --- | --- |
| RF1 | Mostrar actividades con día, horario, edades, profesor y cuota mensual. | A: sitio público; B/C: actividades | Catálogo JS generado desde objetos y clase Actividad; datos completos. | Verificar los campos de las ocho actividades iniciales. | Pendiente |
| RF2 | Filtrar por disciplina y edad. | A: catálogo; C/D: catálogo socios | DOM y addEventListener; filtro React por disciplina y API. | Combinar filtros, edades límite y resultados vacíos. | Pendiente |
| RF3 | Recibir preinscripciones con formulario validado. | A: preinscripcion.html | Obligatorios, email y DNI; errores junto a campos. Definir recepción sin inventar un endpoint obligatorio del Anexo A. | Enviar datos válidos e inválidos; comprobar bloqueo y mensajes. | Pendiente |
| RF4 | Alta, modificación, baja y listado de actividades. | B: MVC/Thymeleaf; C: REST; servicio compartido | ABM, vistas y endpoints; impedir eliminación con inscriptos activos. | Crear, consultar, editar, eliminar; comprobar 404 y 409 con activos. | Pendiente |
| RF5 | Alta, modificación y listado de socios; búsqueda por apellido y paginación. | B: socios MVC, servicio y repositorio | Vistas, búsqueda por apellido y Pageable. | Crear/editar; buscar apellido parcial ignorando mayúsculas y recorrer páginas. | Pendiente |
| RF6 | Inscribir socio aplicando reglas de negocio. | B/C: controladores; servicio de inscripciones; D: Axios | RN1–RN5 una sola vez en servicio; errores MVC y JSON 409. | Éxito y rechazo independiente por cada RN1–RN5. | Pendiente |
| RF7 | Ver inscriptos activos y cupo disponible. | B: Thymeleaf y servicio; C/D: cupo | Consulta de activos y cálculo del cupo disponible. | Excluir BAJA del listado y conteo; comprobar cupo tras baja. | Pendiente |
| RF8 | Ingresar con DNI y consultar inscripciones. | D: ingreso/contexto; C: API socios | Búsqueda DNI, contexto compartido e inscripciones ACTIVAS. | DNI existente/inexistente; navegación con contexto y lista vacía. | Parcial: React local; backend pendiente |
| RF9 | Baja lógica de inscripción en estado BAJA. | B/C: servicio; D: MisActividades | Conservar registro y cambiar estado; permitir baja a MOROSO. | Comprobar BAJA persistida, exclusión de activas y liberación de cupo. | Parcial: React local; backend pendiente |
| RF10 | Mostrar cuota mensual estimada. | D: ResumenCuota y src/utils | Suma de actividades activas y RN6; PropTypes. | Totales para 0, 1, 2 y 3 actividades; excluir bajas. | Parcial: React local; backend pendiente |

## Matriz de reglas — sección 3.2, página 3

| Código | Requisito o regla | Componente responsable | Implementación pendiente | Prueba prevista | Estado |
| --- | --- | --- | --- | --- | --- |
| RN1 | Activos no superan cupo máximo. | InscripcionService | «La actividad no tiene cupo disponible»; bloqueo de actividad. | Último lugar y cupo completo probados; simultaneidad MySQL pendiente. | Servicio implementado; integración pendiente |
| RN2 | Sin dos ACTIVAS del mismo socio en una actividad. | InscripcionService | «El socio ya está inscripto en esta actividad»; conserva BAJA. | Duplicado y reinscripción unitarios aprobados; concurrencia MySQL pendiente. | Servicio implementado; integración pendiente |
| RN3 | Edad actual entre mínimo y máximo inclusive. | InscripcionService | Period y Clock; rango inclusivo confirmado por el usuario. | Límites, cumpleaños y 29/02 unitarios aprobados. | Servicio implementado; integración pendiente |
| RN4 | MOROSO no puede inscribirse; sí consultar y darse de baja. | InscripcionService | «Debe regularizar su cuota en secretaría». | Rechazo unitario aprobado; consulta/baja no implementadas en esta etapa. | Alta implementada; consulta/baja e integración pendientes |
| RN5 | Máximo 3 ACTIVAS por socio. | InscripcionService | «Alcanzó el máximo de 3 actividades»; bloqueo de socio. | Tercera/cuarta unitarios aprobados; BAJA y simultaneidad MySQL pendientes. | Servicio implementado; integración pendiente |
| RN6 | Sumar cuotas activas: 10 % de descuento con 2 y 15 % con 3. | D: calcularCuotaMensual y ResumenCuota | Función pura JavaScript y Jest. | 0, 1, 2 y 3 actividades; descuentos y total. | Implementada en React local; integración pendiente |
| RN7 | DNI de 7 u 8 dígitos numéricos sin puntos. | A: cliente; D: esDniValido | Bloquear envío y error junto al campo; función pura con Jest. | Aceptar 7/8; rechazar puntos, letras, 6 y 9 dígitos. | Implementada en React local; integración pendiente |

## Mínimos que condicionan la arquitectura y quedan pendientes

- Secciones 4, 5 y 8: tres directorios de aplicación, un backend Maven y una base MySQL. MVC y REST comparten servicios. Paquetes separados, sin acceso directo de controlador o vista al repositorio. Estructura preparada; capas de negocio pendientes.
- Parte A: tres páginas, HTML semántico, CSS externo responsivo, JS externo; catálogo con clase Actividad y método, DOM, eventos, filtros, validación y objeto del navegador justificado. Se crean páginas iniciales; catálogo, formularios y requisitos funcionales pendientes.
- Parte B: entidades, relaciones, repositorios derivados, @Query y Pageable implementados. Servicios RN1–RN5, gestión y pantallas de negocio pendientes. Pruebas MySQL preparadas, sin ejecución real.
- Parte C/Anexo A: nueve endpoints con JSON, verbos y estados 200/201/204/404/409; CORS para localhost:3000 y colección Postman. Configuración CORS preparada; endpoints y pruebas pendientes. Resolver ciclos de serialización al implementar relaciones, sin afirmar que ya se observaron.
- Parte D: rutas, componentes, props/PropTypes, hooks, contexto y gestión visual del PR #2 con datos locales. Conexión Axios a la API pendiente; no se alteró React.
- Parte E: PR #2 aporta pruebas Jest y React Testing Library. Postman y seis escenarios de sistema pendientes; ver [casos](pruebas-sistema.md). Integridad Java y MVC no sustituyen esos mínimos ni acreditan persistencia.
- Sección 3.3: schema.sql y seed.sql implementados con ocho actividades y 21 socios, dos MOROSOS, Yoga casi completa e historial BAJA. Preparación manual confirmada por el usuario; conteos y persistencia pendientes de comprobación autenticada por Codex.
- Parte F: README con integrantes, creación de base, ejecución y DNI de prueba; informe PDF de 8–12 páginas, arquitectura, modelo, endpoints, capturas, pruebas y conclusiones. Integrantes pendientes de informar; DNI preparados en datos-modelo.md; informe pendiente.
- Sección 7: cinco entradas reales de IA como mínimo, al menos tres con errores o ajustes detectados por el grupo. No completar con hechos ficticios; las correcciones automáticas no se atribuyen al grupo.

## Interpretaciones documentadas

La introducción de sección 3.2 ubica las reglas en backend salvo RN7, pero la fila RN6 exige expresamente JavaScript y Jest. Se sigue la especificación concreta y el diagrama: RN1–RN5 en servicios, RN6 en JavaScript y RN7 en cliente.

El formato de bitácora está en Anexo C, páginas 13–14. Las referencias a Anexo D en secciones 5 y 7 no coinciden con el anexo existente.

Prompt 01 autorizó la inicialización. La solicitud del 2026-11-07 autorizó modelo, repositorios, SQL, datos, pruebas y documentación. El cierre del 2026-11-08 autoriza commit y push de feature/backend-modelo-datos y PR hacia dev, sin merge, despliegue, servicios RN1–RN5, controladores nuevos ni cambios en frontends.

## Requisito adicional de alojamiento

Preparación para Cloudflare Pages (frontends), Render (Spring Boot y Thymeleaf) y Aiven MySQL. Configuración por entorno, Dockerfile, blueprint Render y fallback SPA preparados, sin despliegue. TLS, credenciales, dominios, conexión a Aiven e imagen Docker requieren validación futura. Ver [arquitectura](arquitectura.md), [alojamiento](alojamiento.md) y [validación inicial](validacion-inicial.md).

## Resultado final de la etapa RN1–RN5 — 2026-11-08

El usuario ejecutó las suites MySQL y verify exitosamente. Codex verificó XML: 36 aprobadas, 0 fallos, 0 errores, 0 omitidas, incluyendo las cuatro pruebas de integración del servicio (BAJA/historial y tres escenarios de concurrencia). Quedan superados los pendientes de ejecución MySQL de la matriz de esta etapa. RN1–RN5 implementadas y probadas a nivel servicio; RF integrales siguen requiriendo controladores e integración. No se implementaron consulta/baja de RN4 ni nuevos endpoints. Ver reglas-negocio.md para evidencia y límites.

## Etapa MVC de Secretaría — 2026-11-08

Se implementó el lado MVC de RF4–RF7 y la baja lógica RF9: ABM de actividades (sin eliminar historial), alta/edición/listado/búsqueda por apellido de socios, inscripción mediante RN1–RN5, consulta por actividad/estado y cupos. Los listados se paginan. Los filtros de edad del catálogo público/React y la API siguen fuera de esta etapa; no se declara cumplimiento integral de los RF que requieren esos componentes.

Verify actual sin MySQL: 58 aprobadas, 0 fallos, 0 errores y 13 omitidas, BUILD SUCCESS. Los resultados anteriores se conservan como historia. Dos pruebas MySQL nuevas de baja/consulta y administración están compiladas pero pendientes de ejecución autenticada junto con las anteriores. Sin cambios de esquema, datos de desarrollo, frontends o despliegue. Ver modulo-mvc.md para rutas, validación, diseño, límites y revisión humana.

## Etapa API REST — 2026-11-08

Se contrastó el PDF original, Anexo A página 13, y se implementaron sus nueve endpoints exactos. CRUD de actividades, socio por DNI, ACTIVAS por socio, alta con RN1–RN5 y baja lógica PUT. DTOs explícitos evitan ciclos; servicios compartidos preservan reglas y MVC. La API no incorpora CRUD de socios adicional. Filtros/JSON permiten sustituir posteriormente datos simulados de React, pero no se lo modificó ni se afirma integración ejecutada.

Verify actual: 95 aprobadas, 0 fallos/errores y 14 MySQL omitidas; BUILD SUCCESS. Postman tiene colección importable y entorno, sin ejecución todavía. RF integrales que dependen de Axios y pruebas de sistema continúan parciales; no confundir disponibilidad de endpoints con integración final. API sin autenticación/CSRF, uso local. Contrato y evidencia en api-rest.md. No hay commit/push/merge o despliegue en esta etapa.

## Mejora voluntaria: vigencia del socio — preparada el 2026-11-08

RF5 no exige baja del socio; RF9 es baja de inscripción. La mejora de EstadoSocio y baja con historial fue propuesta y aprobada por el usuario. Nueve endpoints del anexo más PUT /api/socios/{id}/baja; no se renumeran RN1–RN5. El código adicional y schema/migración están preparados por Codex; ejecución de DDL expresamente no autorizada. React no cambia. Verify actual sin MySQL: 111 aprobadas, 0 fallos/errores, 17 omitidas. Esquema nuevo y pruebas autenticadas pendientes; ver baja-logica-socios.md.

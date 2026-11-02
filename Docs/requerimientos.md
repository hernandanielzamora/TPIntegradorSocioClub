# Requerimientos y trazabilidad — SocioClub

Fuente principal: [TPIntegradorSocioClubEnunciado.pdf](Enunciado/TPIntegradorSocioClubEnunciado.pdf), leído íntegramente (14 páginas) el 2026-11-02.

Estado: inicialización estructural según Prompt 01. Todos los RF y RN están **Pendientes** de implementación y prueba funcional. Las páginas iniciales y configuración no acreditan cumplimiento de requisitos de negocio.

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
| RF8 | Ingresar con DNI y consultar inscripciones. | D: ingreso/contexto; C: API socios | Búsqueda DNI, contexto compartido e inscripciones ACTIVAS. | DNI existente/inexistente; navegación con contexto y lista vacía. | Pendiente |
| RF9 | Baja lógica de inscripción en estado BAJA. | B/C: servicio; D: MisActividades | Conservar registro y cambiar estado; permitir baja a MOROSO. | Comprobar BAJA persistida, exclusión de activas y liberación de cupo. | Pendiente |
| RF10 | Mostrar cuota mensual estimada. | D: ResumenCuota y src/utils | Suma de actividades activas y RN6; PropTypes. | Totales para 0, 1, 2 y 3 actividades; excluir bajas. | Pendiente |

## Matriz de reglas — sección 3.2, página 3

| Código | Requisito o regla | Componente responsable | Implementación pendiente | Prueba prevista | Estado |
| --- | --- | --- | --- | --- | --- |
| RN1 | Activos no superan cupo máximo. | Servicio de inscripciones | Rechazo: «La actividad no tiene cupo disponible». | Aceptar último lugar, rechazar siguiente y prever solicitudes simultáneas. | Pendiente |
| RN2 | Sin dos ACTIVAS del mismo socio en una actividad. | Servicio de inscripciones | Rechazo: «El socio ya está inscripto en esta actividad». | Repetir alta activa y comprobar ausencia de duplicación. | Pendiente |
| RN3 | Edad actual entre mínimo y máximo inclusive. | Servicio de inscripciones | Calcular desde nacimiento a fecha de hoy; rechazo indicando rango. | Límites, fuera de rango y cumpleaños pendiente/cumplido. | Pendiente |
| RN4 | MOROSO no puede inscribirse; sí consultar y darse de baja. | Servicio de inscripciones | Rechazo: «Debe regularizar su cuota en secretaría». | Rechazar inscripción; permitir consulta y baja a MOROSO. | Pendiente |
| RN5 | Máximo 3 ACTIVAS por socio. | Servicio de inscripciones | Rechazo: «Alcanzó el máximo de 3 actividades». | Aceptar tercera, rechazar cuarta y permitir nueva tras baja. | Pendiente |
| RN6 | Sumar cuotas activas: 10 % de descuento con 2 y 15 % con 3. | D: calcularCuotaMensual y ResumenCuota | Función pura JavaScript y Jest. | 0, 1, 2 y 3 actividades; descuentos y total. | Pendiente |
| RN7 | DNI de 7 u 8 dígitos numéricos sin puntos. | A: cliente; D: esDniValido | Bloquear envío y error junto al campo; función pura con Jest. | Aceptar 7/8; rechazar puntos, letras, 6 y 9 dígitos. | Pendiente |

## Mínimos que condicionan la arquitectura y quedan pendientes

- Secciones 4, 5 y 8: tres directorios de aplicación, un backend Maven y una base MySQL. MVC y REST comparten servicios. Paquetes separados, sin acceso directo de controlador o vista al repositorio. Estructura preparada; capas de negocio pendientes.
- Parte A: tres páginas, HTML semántico, CSS externo responsivo, JS externo; catálogo con clase Actividad y método, DOM, eventos, filtros, validación y objeto del navegador justificado. Se crean páginas iniciales; catálogo, formularios y requisitos funcionales pendientes.
- Parte B: Spring Initializr, Web MVC, Thymeleaf, JPA, MySQL; entidades Socio, Actividad e Inscripcion con anotaciones y relaciones JPA; repositorios con dos métodos derivados y una @Query; Pageable, RN1–RN5 y vistas con fragmentos y atributos Thymeleaf. Dependencias y pantalla inicial preparadas; modelo, repositorios y gestión pendientes.
- Parte C/Anexo A: nueve endpoints con JSON, verbos y estados 200/201/204/404/409; CORS para localhost:3000 y colección Postman. Configuración CORS preparada; endpoints y pruebas pendientes. Resolver ciclos de serialización al implementar relaciones, sin afirmar que ya se observaron.
- Parte D: CRA, BrowserRouter, Axios centralizado; rutas /, /actividades, /actividades/:id y /mis-actividades. Componentes Encabezado, Pie, TarjetaActividad, ListaActividades, DetalleActividad, MisActividades y ResumenCuota; props/PropTypes, useState, useEffect y contexto. Solo pantalla inicial y cliente Axios sin peticiones; resto pendiente.
- Parte E: mínimo seis pruebas Jest de calcularCuotaMensual/esDniValido y todos los bordes pedidos; dos React Testing Library de TarjetaActividad, Postman y seis casos de sistema. Pendientes; ver [plantilla de sistema](pruebas-sistema.md). La prueba MVC de inicialización no sustituye estos mínimos.
- Sección 3.3: cargar las ocho actividades exactas del PDF; al menos 15 socios con edades variadas, 2 MOROSOS como mínimo y una actividad casi completa. Pendiente: schema.sql y seed.sql tienen solo comentarios.
- Parte F: README con integrantes, creación de base, ejecución y DNI de prueba; informe PDF de 8–12 páginas, arquitectura, modelo, endpoints, capturas, pruebas y conclusiones. Integrantes y DNI no informados; informe pendiente.
- Sección 7: cinco entradas reales de IA como mínimo, al menos tres con errores o ajustes detectados por el grupo. No completar con hechos ficticios; las correcciones automáticas no se atribuyen al grupo.

## Interpretaciones documentadas

La introducción de sección 3.2 ubica las reglas en backend salvo RN7, pero la fila RN6 exige expresamente JavaScript y Jest. Se sigue la especificación concreta y el diagrama: RN1–RN5 en servicios, RN6 en JavaScript y RN7 en cliente.

El formato de bitácora está en Anexo C, páginas 13–14. Las referencias a Anexo D en secciones 5 y 7 no coinciden con el anexo existente.

El usuario proporcionó Prompt 01 en esta sesión: autoriza exclusivamente monorepo, tres aplicaciones mínimas, configuración y documentación. No autoriza CRUD, entidades, tablas, datos ni reglas.

## Requisito adicional de alojamiento

Preparación para Cloudflare Pages (frontends), Render (Spring Boot y Thymeleaf) y Aiven MySQL. Configuración por entorno, Dockerfile, blueprint Render y fallback SPA preparados, sin despliegue. TLS, credenciales, dominios, conexión a Aiven e imagen Docker requieren validación futura. Ver [arquitectura](arquitectura.md), [alojamiento](alojamiento.md) y [validación inicial](validacion-inicial.md).

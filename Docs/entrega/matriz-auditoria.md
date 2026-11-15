# Matriz de auditoría final — Grupo 3

Fuente: PDF original de la consigna, 14 páginas. Estado RF3 actualizado con pruebas reales en MySQL nuevo aislado 3308. Originales y MySQL84 sin cambios. No se estima calificación docente.

| Requisito | Archivo responsable | Implementado | Probado | Evidencia | Observaciones |
| --- | --- | --- | --- | --- | --- |
| RF1 | sitio-publico/js/script.js | Sí | Node + Chrome | 29 tests; catálogo real capturado | Ocho actividades iniciales, no vacantes en vivo |
| RF2 | sitio-publico/js/script.js | Sí | Filtros combinados y bordes | Node + sitio-filtros.png | React filtra disciplina según Parte D |
| RF3 | PreinscripcionService.java; preinscripcion.html | Sí | 201 + SQL + bandeja | rf3-recepcion.json; Newman; Playwright | No crea socio ni consume cupo; migración 004 |
| RF4 | ActividadService.java; SecretariaController.java | Sí | Unitarios/MVC/API | 136 backend; ABM Newman | FK conserva historial y solicitudes |
| RF5 | SocioService.java; SocioRepository.java; socios.html | Sí | MVC y Chrome | Backend + secretaria-socios.png | Búsqueda y paginación implementadas |
| RF6 | InscripcionService.java; Detalle.js | Sí | Sistema real | PS01–PS06 APROBADOS | RN1–RN5 compartidas entre MVC/REST |
| RF7 | ConsultaSecretariaService.java; ApiConsultaService.java | Sí | API y sistema | Cupos previos/posteriores y capturas | Solo cuentan ACTIVAS |
| RF8 | Ingreso.js; SocioContext.js; api.js | Sí | Playwright real | Ingreso de seis DNI ficticios | Identificación académica, no autenticación robusta |
| RF9 | InscripcionService.java; MisActividades.js | Sí | Baja real desde React | HTTP 200, lista vacía y fila BAJA | Conserva historial |
| RF10 | ResumenCuota.js; calcularCuotaMensual.js | Sí | Jest y Chrome | Subtotal 68000, total 57800 | Precios y descuentos comprobados |
| RN1 | InscripcionService.java | Sí | Unitarios + sistema + Newman | casos-sistema-ejecutados.json | Cupo completo, 409 sin alta |
| RN2 | InscripcionService.java | Sí | Unitarios + sistema + Newman | casos-sistema-ejecutados.json | Duplicado, 409 sin alta |
| RN3 | InscripcionService.java | Sí | Unitarios + sistema + Newman | casos-sistema-ejecutados.json | Edad fuera de rango, 409 sin alta |
| RN4 | InscripcionService.java | Sí | Unitarios + sistema + Newman | casos-sistema-ejecutados.json | MOROSO, 409 sin alta |
| RN5 | InscripcionService.java | Sí | Unitarios + sistema + Newman | casos-sistema-ejecutados.json | Tres ACTIVAS, cuarta rechazada |
| RN6 | calcularCuotaMensual.js; ResumenCuota.js | Sí | Jest + Chrome | react-cuota.png | 0–3 actividades, descuentos y centavos |
| RN7 | script.js; esDniValido.js; PreinscripcionDtos.java | Sí | Node/Jest/Chrome/API | RN7 público y React; HTTP 400 | 7 u 8 dígitos sin puntos |
| A: conceptos HTML/CSS/JS | sitio-publico/ | Sí | 29 tests y Playwright | Clase, DOM, eventos, location, alt y media queries | Tres páginas, CSS/JS externos, accesibilidad |
| B: capas, entidades y repositorios | backend/src/main/ | Sí | 136 tests, 18 MySQL | Doce clases de pruebas completas | JPA, @Query, Pageable y fragmentos |
| C: Anexo A, DTO, HTTP y CORS | ApiController.java; ApiDtos.java; WebConfig.java | Sí | MockMvc + Newman + OPTIONS real | Nueve contratos; 25 requests/34 aserciones | RF3 y baja de socio adicionales, no mínimos inventados |
| D: React exigido | app-socios/src/ | Sí | 58 tests y build; Chrome real | Cuatro rutas, hooks, contexto, Axios, PropTypes | Sin RN8 ni panel admin React |
| Datos y SQL | schema.sql; seed.sql; migrations/004_preinscripcion.sql | Sí | MySQL aislado | Seed nuevo, 21 socios, 2 MOROSOS, ocho actividades | 004 idempotente probada; originales intactos; 002/003 no ejecutados |
| E: seis casos de sistema | Docs/pruebas-sistema.md | Sí | Seis aprobados | JSON + seis capturas | Canal UI salvo POST de RN1 con botón deshabilitado |
| F: informe y capturas | Docs/entrega/ | Sí | PDF 12 páginas; revisión renderizada | Tres interfaces; 33 PNG; link dev y bitácora final | DOCX y Markdown reales |
| F: bitácora IA | Docs/bitacora-ia.md; capítulo final | Sí | Hechos y autoría revisados | AH-1/AH-2/AH-3, 1064 y cierre real | No atribuir a humanos código de Codex |
| Historial de cinco integrantes | Git log / shortlog | Parcial | Todos los refs revisados | Solo Hernán, Matias y Mateo con aportes | Usuario confirmó ausencia de aportes de Fernando/Joaquín y pidió continuar |
| Acceso docente | GitHub /tree/dev | Sí | Consulta anónima API GitHub | Repositorio público | No se cambiaron permisos; main sin actualizar |
| Paquete | herramientas/empaquetar.py | Se genera tras commit | ZIP/extracción/SHA | entrega-final/manifest-entrega.txt | Preserva entrega anterior y excluye secretos/generados |

Los nombres Java corresponden a backend/src/main/java/ar/edu/siglo21/socioclub/; las vistas a backend/src/main/resources/templates/secretaria/; los componentes React a app-socios/src/.

**A — Obligatorio bloqueante técnico:** ninguno en los recorridos ejecutados. **B — Validaciones:** completadas las automatizadas, Postman/Newman, seis casos, RF3 SQL y capturas. **C — Presentación:** PDF/DOCX/ZIP y salvedad académica de participación explícita; main requiere autorización expresa y la entrega apunta a dev. **D — Opcional:** RN8, panel administrativo React, despliegue y autenticación robusta no implementados ni agregados en este cierre.

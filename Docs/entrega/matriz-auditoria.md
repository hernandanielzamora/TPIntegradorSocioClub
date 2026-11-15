# Matriz de auditoría del cierre — Grupo 3

Fuente primaria: PDF original `Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf`, 14 páginas, leído nuevamente en este cierre. Base: `origin/dev` 874c6bb. Resultados nuevos en [verificaciones.json](verificaciones.json); los documentos anteriores conservan antecedentes históricos.

| Requisito | Archivo responsable | Implementado | Probado | Evidencia | Observaciones |
| --- | --- | --- | --- | --- | --- |
| RF1 catálogo completo | sitio-publico/js/script.js | Sí, ocho actividades iniciales | Sí, Node/DOM | 25 tests, sitio-catalogo.png | No son vacantes MySQL actuales |
| RF2 disciplina y edad | sitio-publico/js/script.js | Sí | Sí, combinado y bordes | Tests Node, sitio-filtros.png | React filtra por disciplina según Parte D |
| RF3 recibir preinscripciones | sitio-publico/preinscripcion.html; js/script.js | **Parcial: solo valida** | Validación sí; recepción no existe | Tests y captura de formulario | **A: bloqueante**; propuesta en rf3-decision-pendiente.md |
| RF4 ABM de actividades | backend/.../service/ActividadService.java; controller/mvc/SecretariaController.java | Sí | Unitarios y MockMvc | SecretariaServiceTests, SecretariaControllerTests | B: recorrido persistente actual pendiente |
| RF5 socios, búsqueda y paginación | backend/.../service/SocioService.java; repository/SocioRepository.java; templates/secretaria/socios.html | Sí | Unitarios y MVC | Selección backend nueva | B: recorrido manual actual pendiente |
| RF6 inscripciones | backend/.../service/InscripcionService.java | Sí | Unitarios, API y React mock | 20 tests servicio, API/RTL | B: PS01–PS06 pendientes |
| RF7 inscriptos y cupo | backend/.../service/ConsultaSecretariaService.java; ApiConsultaService.java | Sí | Servicio y MVC/API | Selección backend nueva | Cupo cuenta solo ACTIVAS |
| RF8 DNI y ACTIVAS | app-socios/src/paginas/Ingreso.js; contexto/SocioContext.js; servicios/api.js | Sí | RTL y API; ingreso real | react-ingreso.png; 58 tests | B: falta recorrido con MySQL |
| RF9 baja lógica | backend/.../service/InscripcionService.java; app-socios/src/paginas/MisActividades.js | Sí | Unitarios/MockMvc/RTL | Selección nueva | No ejecutar baja real sin datos autorizados |
| RF10 cuota estimada | app-socios/src/componentes/ResumenCuota.js; utils/calcularCuotaMensual.js | Sí | Jest/RTL | 58 tests | B: falta captura con socio identificado |
| RN1 cupo | backend/.../service/InscripcionService.java | Sí | Unitario/API/RTL | InscripcionServiceTests; ApiControllerTests | E2E RN1 pendiente |
| RN2 duplicados | Mismo servicio | Sí | Unitario/API/RTL | Misma selección | E2E RN2 pendiente |
| RN3 edad inclusiva actual | Mismo servicio | Sí | Bordes, cumpleaños, bisiesto | 20 tests servicio | E2E RN3 pendiente |
| RN4 MOROSO | Mismo servicio | Sí | Unitario/API/RTL | 409 y regla en MockMvc | Consulta y baja siguen permitidas |
| RN5 máximo tres ACTIVAS | Mismo servicio | Sí | Unitario/API/RTL | Prueba cuarta actividad | Concurrencia MySQL no repetida |
| RN6 bonificaciones | app-socios/src/utils/calcularCuotaMensual.js | Sí | Jest, 0–3 y centavos | 58 tests React | 10 % con dos, 15 % con tres |
| RN7 DNI | sitio-publico/js/script.js; app-socios/src/utils/esDniValido.js | Sí | Node/Jest y Chrome | react-error-dni.png | No confundir validación con recepción |
| A: tres HTML semánticos, externos, media query | sitio-publico/*.html; css/estilos.css | Sí | DOM y Chrome | 25 tests; 8 capturas públicas | Ilustración original y alt; ubicación general del caso |
| A: clase, método, eventos, objeto navegador | sitio-publico/js/script.js | Sí | Node/jsdom | Actividad, esAptaParaEdad, location | Sin framework nuevo |
| B: capas, JPA y relaciones | backend/src/main/java/ar/edu/siglo21/socioclub/ | Sí | 111 tests sin MySQL | Ocho suites seleccionadas | No vuelve a ejecutar suites de base vacía |
| C: nueve endpoints Anexo A | backend/.../controller/api/ApiController.java | Sí | MockMvc y contrato estático | 34 tests API; auditar.py | PUT baja de socio es adicional existente |
| C: DTO, HTTP y errores JSON | dto/ApiDtos.java; controller/api/ErroresApiController.java | Sí | MockMvc | 200/201/204/400/404/409 | No confirma escritura MySQL nueva |
| C: CORS | backend/.../config/WebConfig.java | Sí | OPTIONS MockMvc permitido/ajeno | ApiControllerTests | HTTP backend real no repetido |
| C/E: Postman | postman/SocioClub.postman_collection.json | Sí: 22 requests | JSON y nueve contratos; runner no | verificaciones.json | **B: ejecución real pendiente** |
| D: rutas, componentes, props, hooks, contexto | app-socios/src/App.js; componentes/; contexto/ | Sí | Jest/RTL y build | 58 tests y build exitoso | Sin panel admin React ni RN8 |
| Modelo y seed | database/schema.sql; seed.sql; migrations/001_estado_socio.sql | Sí | Lectura estática y tests de modelo | 21 socios, 2 MOROSOS, 8 actividades; Yoga 14/15 en script | No ejecución SQL, sin conteos nuevos de base |
| E: seis casos de sistema | Docs/pruebas-sistema.md | Procedimientos sí | **E2E no ejecutados** | PS01–PS06 preparados | **B: evidencia real pendiente** |
| F: PDF 8–12, link y bitácora final | Docs/entrega/TPI_SocioClub_Grupo3.pdf | Sí: 10 páginas | Parser y revisión de las 10 páginas renderizadas | Generador y PDF real | Capturas de Secretaría faltantes impiden cumplimiento visual completo |
| F: cinco integrantes y grupo | README.md; informe | Sí, confirmación humana | Contrastado con mensaje del usuario | Grupo 3 y cinco nombres | Sin fecha de entrega por solicitud |
| F: capturas tres interfaces | Docs/entrega/evidencias/ | Parcial: público y React ingreso | Chrome real, 10 PNG | verificacion-navegador.json | **C: Secretaría y React con API pendientes** |
| F: bitácora de IA | Docs/bitacora-ia.md; último capítulo del PDF | Sí, antecedentes reales | Lectura de AH-1/2/3 y 1064 | Mínimo cinco intervenciones sintetizadas | Ajustes humanos operativos; aceptación docente sin garantizar |
| Git, commits de integrantes | git log / shortlog | Tres autores registrados | Historial leído | Hernán, Matias, Mateo | C: no inventar commits de Fernando/Joaquín |
| Acceso del docente | Repositorio GitHub | Por confirmar | No se cambiaron permisos | Requiere consulta del profesor | C: si privado, invitar al docente |
| Paquete limpio | herramientas/empaquetar.py | Se genera tras commit | ZIP, extracción, hashes y PDF | entrega-final/manifest-entrega.txt | Candidato, no constancia de cumplimiento total |

Las rutas `backend/...` abrevian `backend/src/main/java/ar/edu/siglo21/socioclub/`. Las pruebas de servicios usan dobles y MockMvc; no son pruebas del sistema persistido.

Clasificación: **A** obligatorio bloqueante: RF3; **B** validación: E2E/Postman/MySQL y recorrido; **C** documentación/presentación: capturas, acceso docente y contribuciones faltantes; **D** opcional: RN8, administración React, despliegue y seguridad futura. No se trabaja en D.

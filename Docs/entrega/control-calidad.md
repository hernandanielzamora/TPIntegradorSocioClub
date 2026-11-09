# Control documental — informe y defensa

Revisión final: 9 de noviembre de 2026; trabajo iniciado el 8. Base: dev/origin/dev 4570207cc4906fdc4863c3ba0c27741e0e32cfd9, sincronizados al crear feature/documentacion-entrega-defensa. Solo documentación y recursos, sin cambios funcionales.

## Entregables existentes

| Documento | Archivos | Resultado |
| --- | --- | --- |
| Informe | informe-tecnico-final.md, informe-tecnico-final.docx, TPI_SocioClub_GrupoN.pdf | 11 páginas PDF; enlace GitHub activo en portada; siete entradas de IA como último capítulo |
| Guía | ../defensa/guia-integral-defensa.md, .docx, .pdf | 32 páginas PDF; veinte módulos; sesenta preguntas y sesenta repreguntas; más de 13.000 palabras |
| Guion | ../defensa/guion-presentacion.md | Reparto editable entre cuatro; 2 + 7 + 3 minutos, más defensa/devolución posteriores |
| Diagramas | ../recursos/arquitectura, modelo-er, secuencia-inscripcion, cada uno SVG/PNG | Esquemas del código real, no capturas |
| Exportación | ../recursos/generar_documentos.py y verificar_documentos.py | Herramientas documentales aisladas de dependencias de aplicación |

GrupoN, fecha y nombres completos restantes quedan pendientes por decisión del usuario. Los DOCX son editables y se comprobó integridad ZIP/XML e imágenes; no se abrió Word gráficamente y sus saltos al reexportar requieren revisión. El PDF se generó desde Markdown con fuentes incrustadas y saltos explícitos. Completar capturas requiere regenerar y revisar otra vez.

## Comprobaciones realizadas

- Lectura completa del PDF original, catorce páginas. Parte F/sección 7 dicen Anexo D; el formato está rotulado C en páginas 13–14. Exige cinco entradas y tres errores o ajustes detectados por el grupo, sin exigir literalmente tres ediciones manuales de fuente.
- Contraste de modelo, SQL, reglas, locks, controladores, DTO, CORS, templates, React/Axios, RN6/RN7 y DOM con código final. Dieciocho extractos de la guía localizados automáticamente en archivos reales, ignorando solo espacios.
- Informe PDF: 11 páginas, enlace activo en primera, bitácora última, imágenes y tablas presentes. Once páginas renderizadas e inspeccionadas en mosaico y ampliación de tablas de endpoints/bitácora: sin recortes observados. Control geométrico adicional sin texto fuera de página.
- Guía PDF: 32 páginas, mosaico completo inspeccionado; veinte módulos y sesenta preguntas/seguimientos secuenciales comprobados.
- Enlaces locales de imágenes resueltos; Markdown UTF-8 sin caracteres de reemplazo; DOCX ZIP/XML válidos. Scripts documentales ejecutados correctamente. No se afirma certificación ortográfica por herramienta externa: hubo revisión de redacción y consistencia técnica.
- backend/.env ignorado por .gitignore y ausente del índice, sin leer su contenido. Los exportadores no lo abren ni ejecutan aplicación o SQL.

Controles geométricos/ZIP no sustituyen lectura humana del contenido ni apertura final en Word. Resultado técnico de documentación, no certificación de entrega completa.

## Fuentes y trazabilidad

Enunciado, README, documento-maestro-proyecto, cierre-desarrollo, arquitectura, requerimientos, pruebas-sistema, preparacion-informe-defensa, bitacora-ia, registro-trabajo, mejoras-futuras y documentación MVC/API/React/sitio/datos/migración. Párrafos históricos no se tratan como estados vigentes cuando el código o cierre posterior los supera.

Código: entidades/repositorios Socio/Actividad/Inscripcion; InscripcionService, SocioService, ActividadService, ApiConsultaService y ConsultaSecretariaService; controladores/advice, DTO y WebConfig; application.properties y templates. React: App, SocioContext, componentes, cuatro páginas, servicios/api, utils y tests. Sitio: tres HTML, CSS, script y tests. SQL: schema, seed, 001 y fixture convencional; 002/003 son propuestas. Postman: JSON e instrucciones, sin runner.

Git registra autores Mateo Hernandez y Matias Jure, con hashes citados en guía. No se usan para completar portada contra la decisión del usuario ni como prueba de autoría manual. No se verificó apellido/contribución de Fernando en las fuentes consultadas. Los commits bajo cuenta del usuario pueden incluir generación Codex y no se atribuyen como programación personal.

## Evidencia anterior, sin nuevas pruebas funcionales

128 backend históricos autenticados antes del fixture; 111 posteriores sin MySQL; 58 React; 25 sitio; build y HTTP/CORS según cierre-desarrollo. No sumar 128+111. Esta tarea no ejecutó Maven, Jest, Node tests, SQL, migraciones ni escrituras HTTP. No se modificaron reportes ni se inventa otra aprobación. JSON Postman válido no equivale a runner aprobado.

## Pendientes y riesgos académicos

1. Nombres completos, grupo y fecha real de entrega.
2. Cuatro capturas auténticas: sitio, Secretaría, React y regla rechazada con contexto.
3. Seis casos integrales con obtenido/evidencia, Postman real y revisión móvil/teclado.
4. Aclarar recepción RF3: hoy solo validación local.
5. Confirmar valoración docente de AH-1–AH-3/IH-MIG-01: son intervenciones reales operativas/diseño, no automáticamente tres correcciones manuales de fuente.
6. Revisar contribuciones de todos según requisito de commits; no fabricar historia ni atribuciones.
7. Lectura humana de los entregables y comprensión individual.

El sistema sigue local, sin seguridad administrativa robusta, autorización ni CSRF; DNI no autentica; RN8/admin postergados. Respaldos confirmados sin restore de ensayo. No limpiar testing para reproducir suites que exigen vacío. No se presenta el informe como listo para aula.

## Git

Publicación autorizada limitada a la rama documental, sin PR/merge ni cambios a main/dev. El hash y sincronización efectivos se comprueban después del commit y se informan al cierre; no se anticipan aquí. Revisar staging y diff --check antes de publicar, sin incluir herramientas .cache, logs o secretos.

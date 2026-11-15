# Checklist final — Grupo 3

**LISTO PARA ENTREGAR en lo técnico, con salvedad académica de participación aceptada por el usuario.** El historial acredita aportes de Hernán, Matias y Mateo. El usuario confirmó que Fernando y Joaquín no realizaron aportes y pidió continuar; no se inventan autorías ni commits. No se estima calificación docente.

| Categoría | Evidencia actual | Cumplimiento y límites |
| --- | --- | --- |
| A · Público · 10 pts | Tres HTML, clase JS/DOM, filtros, validación y recepción RF3; 29 tests | Implementado y probado; el catálogo inicial no representa vacantes actuales. |
| B · Spring/JPA · 25 pts | Capas, cuatro entidades, ABM, paginación, reglas, bandeja y capturas | 136 pruebas backend, incluidas 18 MySQL; JAR empaquetado. |
| C · REST · 15 pts | Nueve mínimos más baja de socio y RF3, DTO, HTTP y CORS | MockMvc y Newman real: 25 peticiones, 34 aserciones aprobadas. |
| D · React · 25 pts | Rutas, contexto, Axios, props/hooks, 58 tests y build | Playwright comprobó ingreso, inscripción, rechazos, baja y cuota. |
| E · Pruebas · 10 pts | PS01–PS06 reales con datos y resultados; Newman | Todos aprobados en nueva instancia aislada; no en bases originales. |
| F · Docs/IA · 15 pts | PDF 12 páginas, DOCX/MD, diagramas, 33 PNG y bitácora final | Completo en documentación; salvedad de participación de solo tres integrantes. |

- [x] Enunciado original de 14 páginas leído y contrastado con A–F, RF/RN y anexos.
- [x] Grupo 3, cinco nombres confirmados y ninguna fecha de entrega.
- [x] RF3: formulario → POST 201 → fila SQL → bandeja paginada.
- [x] Preinscribir no crea socios, no consume cupos ni expone contactos mediante GET público de API.
- [x] Migración 004 aditiva ejecutada dos veces sin cambiar datos previos.
- [x] 136 backend, 58 React, 29 sitio, build React y JAR aprobados.
- [x] PS01–PS06 ejecutados; alta/baja React y RN6 con datos reales adicionales.
- [x] Newman: 25 peticiones, 34 aserciones, cero fallos.
- [x] Capturas auténticas de las tres interfaces; variantes móviles y desborde corregido.
- [x] PDF 12 páginas, enlace clicable a dev en portada y bitácora al final; páginas renderizadas y revisadas.
- [x] Repositorio público y acceso anónimo comprobado.
- [x] Instancia nueva 3308 elegida por el usuario; MySQL84 y las bases originales intactos.
- [x] Sin SQL 002/003, RN8, panel administrativo React ni despliegue público.
- [x] Salvedad de participación comunicada y aceptada, sin falsificar historial.

**main no se actualizó:** requiere autorización expresa. La entrega utiliza dev y el manifiesto identifica el commit exacto. El ZIP conserva la versión anterior, excluye secretos/generados y se verifica mediante apertura, extracción y hashes. Los resultados efectivos y SHA-256 quedan en entrega-final/manifest-entrega.txt y verificacion-paquete.json.

Sobre una base anterior se debe respaldar y aplicar 004 antes de iniciar esta versión. No se aplicó a las bases originales por elección del usuario. Una base nueva usa schema.sql y seed.sql una sola vez.

Al aula virtual: PDF obligatorio. GitHub: rama dev. ZIP: complemento reconstruible. El uso sigue siendo académico y local, con identificación por DNI sin autenticación robusta.

# Documento maestro — SocioClub

Etapa vigente: módulo MVC en feature/backend-mvc-thymeleaf, sin commit/push/merge. Ver [módulo de Secretaría](modulo-mvc.md): panel, ABM de actividades, alta/edición de socios, inscripción y bajas lógicas. Reportes disponibles al cierre: 71 aprobadas, 0 fallos/errores/omisiones. Codex repitió las 58 pruebas sin MySQL, todas aprobadas. Confirmación final del goal verify y recorrido en navegador pendientes. La integración anterior RN1–RN5 a dev quedó en a6b83dc; los estados de publicación anteriores corresponden a esa etapa.

Índice acumulativo del TPI de Taller de Construcción de Software, Universidad Siglo 21. Actualizado el 2026-11-08. No sustituye las fuentes ni duplica el detalle técnico.

| Área | Estado y fuente |
| --- | --- |
| Consigna y trazabilidad | [Requerimientos](requerimientos.md) y PDF en Enunciado; RF integrales todavía dependen de servicios/controladores e integración |
| Arquitectura y estructura | [Arquitectura](arquitectura.md), [estructura](estructura.md) |
| Modelo, SQL y datos ficticios | [Modelo](datos-modelo.md), [datos](datos-ficticios.md), database/schema.sql y seed.sql |
| Persistencia JPA anterior | [Validación](validacion-modelo-datos.md): 13 pruebas y verify aprobados, arranque MySQL y HTTP 200 confirmados por el usuario; integrado mediante PR #3, merge f0633c0 |
| Entorno por computadora | [MySQL local](mysql-local.md), [instalación](instalacion-desde-cero.md), backend/.env.example y scripts/load-env.ps1. .env privado e ignorado |
| Etapa actual RN1–RN5 | [Reglas y pruebas](reglas-negocio.md): InscripcionService, 36 pruebas aprobadas (19 unitarias de servicio, 5 integridad, 1 MVC, 7 persistencia y 4 integración de servicio), 0 fallos/errores/omisiones; verify exitoso confirmado por el usuario y XML inspeccionados por Codex |
| Defensa y autoría | [Guía](guia-defensa-tecnica.md), [bitácora IA](bitacora-ia.md), [trabajo humano](registro-trabajo.md). AH-1/AH-2/AH-3 conservan su alcance de configuración/operación/diseño |
| Pruebas integrales y alojamiento | [Pruebas de sistema](pruebas-sistema.md), [alojamiento](alojamiento.md); no afirmar integración/despliegue completo |

La etapa está auditada en feature/backend-reglas-negocio y autorizada para commit/push y PR hacia dev, sin merge. No modifica main/dev, frontends ni bases. El próximo paso es revisar el PR antes del merge; controladores e integración requieren una nueva etapa.

## Flujo individual de integración directa — 2026-11-08

Decisión humana vigente: desarrollo restante individual, conservando feature/* y dev, sin PR obligatorio. Integrar solo tras validación y confirmaciones necesarias, con merge commit para trazabilidad; no force push, no sobrescribir cambios y detenerse ante conflictos. main permanece fuera de alcance.

Auditoría MVC: los reportes actuales acreditan 71 aprobadas, 0 fallos/errores/omisiones, incluidas las 13 MySQL. Este dato reemplaza el estado histórico de 58/13 del desarrollo inicial; no prueba por sí solo el resultado del goal verify completo. Confirmación final de verify solicitada antes de commit/merge, como instruyó el usuario. Revisión visual manual no confirmada. Seguridad: sin autenticación/autorización ni CSRF implementado; módulo solo local, no apto para exposición pública. Detalle de auditoría y alcance en modulo-mvc.md.

## Confirmación de verify e integración autorizada — 2026-11-08

El usuario confirmó BUILD SUCCESS de Maven verify después de la auditoría. Codex volvió a inspeccionar los siete XML: 71 aprobadas, 0 fallos, 0 errores y 0 omitidas. Esto cierra la espera de confirmación de verify de las secciones anteriores. La ejecución autenticada fue del usuario; no se atribuye a Codex ni implica una revisión visual del navegador no comunicada.

Se continúa con el flujo individual autorizado: commit y push en feature/backend-mvc-thymeleaf, sincronización de dev, merge commit sin forzar y validación pertinente del árbol integrado antes de publicar origin/dev. Los hashes y resultados reales se informarán al completar cada operación; no se anticipa éxito en este registro. main queda fuera de alcance y la feature se conserva. No hay PR obligatorio.

Sin nuevas ediciones manuales de código confirmadas; AH-1/AH-2/AH-3 mantienen su alcance académico. Continúan los límites de seguridad: no hay autenticación/autorización ni protección CSRF implementada; el módulo no está preparado para exposición pública. La comprobación visual completa sigue pendiente. No se agregan funcionalidades ni se alteran bases o frontends.

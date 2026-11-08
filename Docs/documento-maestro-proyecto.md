# Documento maestro — SocioClub

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

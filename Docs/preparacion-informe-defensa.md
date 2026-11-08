# Inventario para informe y futura guía integral de defensa

No es todavía la guía de estudio extensa. Redactarla después del cierre con el hash final de dev, sin anticipar contribuciones o capturas.

| Temas de la futura guía (22) | Fuentes verificables y material a reunir |
| --- | --- |
| 1 problema; 2 qué construimos; 3 arquitectura; 4 tecnologías | Enunciado secciones 1–5, README, arquitectura.md; mostrar tres interfaces y un backend compartido |
| 5 repositorio; 6 MySQL; 7 JPA | estructura.md, schema/seed/migración001, modelo Socio/Actividad/Inscripcion; diagrama PK/FK e historial |
| 8 Controller/Service/Repository; 9 Thymeleaf; 10 REST | SecretariaController, servicios, repositorios, modulo-mvc.md, api-rest.md, nueve endpoints/estados |
| 11 React; 12 Axios; 13 flujo completo | Router, contexto/DNI mínimo, servicios/api.js y cuatro páginas; seguir clic → HTTP → servicio → SQL → DTO |
| 14 RN1–RN7; 15 validaciones | requerimientos.md, InscripcionService, calcularCuotaMensual, esDniValido, Bean Validation/CHECK y script público |
| 16 tests | cierre-desarrollo.md y pruebas-sistema.md; separar 111/58/25 actuales, 128 históricos y E2E pendiente |
| 17 IA; 18 contribuciones reales | bitacora-ia.md, registro-trabajo.md, git log/commits; AH-1–AH-3/IH-MIG-01 sin falsa autoría manual |
| 19 futuro; 20 arranque/demo | mejoras-futuras.md, README y checklist capturas; no exigir RN8/admin React para cerrar |
| 21 preguntas; 22 explicar fragmentos | Preparar respuestas de locks/transacción/dirty checking, DTO vs entidad, useEffect/promesas, DOM/eventos, centavos RN6 y limitación del formulario local |

Cuatro integrantes: completar nombres y responsabilidades reales del historial con revisión humana; no asignar temas como si fueran aportes de código ya realizados. Git conserva los commits del equipo y el trabajo posterior individual informado por el usuario. Si no hay evidencia de tres ajustes humanos al código fuente, mantener esa brecha para revisión docente, no fabricar diffs.

## Esqueleto del informe PDF (propuesta de 10 páginas)

1. Portada, integrantes/grupo/versión final y alcance.
2. Problema, RF y límites (incluida recepción solo local del formulario).
3. Arquitectura de las tres interfaces, tecnología y flujo de datos.
4. Modelo JPA/MySQL, relaciones, restricciones, historial y migración001 manual.
5. Sitio público: clase/DOM/filtros/formulario y captura real.
6. Secretaría/servicio/transacciones y captura real.
7. API REST: nueve endpoints, DTO/estados, ejemplo real sin secretos.
8. React/Axios/contexto/RN6 con captura real e integración.
9. Pruebas actuales/históricas y matriz de seis casos con observado real o pendiente.
10. IA/ajustes humanos/conclusiones/limitaciones/mejoras futuras y referencias.

Reunir capturas del checklist de cierre-desarrollo.md y resultados auténticos antes de exportar. Ajustar a 8–12 páginas según rúbrica, sin rellenar con pruebas o restauraciones no realizadas. No se generó PDF ni capturas en este cierre. La guía integral posterior puede ser extensa y pedagógica, pero debe utilizar la versión efectivamente integrada y las contribuciones confirmadas.

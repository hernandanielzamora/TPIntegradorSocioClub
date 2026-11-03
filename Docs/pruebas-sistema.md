# Pruebas de sistema previstas

Plantilla de los seis escenarios exigidos por la Parte E. Ninguno se ejecutó; requieren funcionalidades y datos que quedan fuera de Prompt 01. Preparar cada escenario de forma independiente para que otras reglas no oculten el resultado buscado.

| Caso | Preparación y pasos previstos | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| --- | --- | --- | --- | --- | --- |
| PS01 — Éxito | Socio AL_DIA, edad válida, menos de 3 activas, sin duplicado; elegir actividad con cupo e inscribir | Inscripción ACTIVA, respuesta 201 y cupo reducido; interfaz actualizada | Sin ejecutar | Pendiente | Pendiente |
| PS02 — RN1 | Preparar actividad llena y socio elegible sin otras restricciones; intentar inscripción | Rechazo 409 con «La actividad no tiene cupo disponible»; sin alta | Sin ejecutar | Pendiente | Pendiente |
| PS03 — RN2 | Socio con una inscripción ACTIVA; actividad con cupo; intentar repetir | Rechazo 409 con «El socio ya está inscripto en esta actividad»; sin duplicación | Sin ejecutar | Pendiente | Pendiente |
| PS04 — RN3 | Socio AL_DIA fuera del rango etario, sin otras restricciones; intentar inscripción | Rechazo 409 indicando el rango permitido; sin alta | Sin ejecutar | Pendiente | Pendiente |
| PS05 — RN4 | Socio MOROSO con edad válida, sin duplicado ni límite y actividad con cupo; intentar inscripción | Rechazo 409 con «Debe regularizar su cuota en secretaría»; consulta y baja siguen permitidas | Sin ejecutar | Pendiente | Pendiente |
| PS06 — RN5 | Socio AL_DIA con 3 activas; intentar cuarta diferente con cupo y edad válida | Rechazo 409 con «Alcanzó el máximo de 3 actividades»; conserva 3 activas | Sin ejecutar | Pendiente | Pendiente |

Al ejecutar, registrar fecha, responsable real, datos utilizados, resultado observado y evidencia. No confundir estas pruebas de sistema pendientes con las comprobaciones de compilación de la inicialización

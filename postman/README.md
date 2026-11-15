# Postman — colección ejecutada

La colección actual tiene **25 peticiones**: nueve endpoints del Anexo A, baja lógica de socios y RF3. En este cierre se ejecutó realmente con **Newman 6.2.3: 34 aserciones aprobadas y cero fallos**. [Resultados por petición](../Docs/entrega/evidencias/newman-resultados.json). Los ejemplos response de la colección son ilustrativos; la evidencia nueva está en ese archivo.

Importar SocioClub.postman_collection.json y SocioClub-local.postman_environment.json. baseUrl es http://localhost:8080. **No ejecutar las escrituras contra una base existente sin seleccionar y aprobar los datos.** La ejecución de cierre utilizó exclusivamente una instancia MySQL nueva en 3308 y un backend dedicado en 8080; no tocó MySQL84 ni sus bases.

Los escenarios se prepararon deliberadamente: Yoga llena para RN1; Martín ya inscripto en Fútbol para RN2; Juan adulto frente a Fútbol infantil para RN3; Federico MOROSO para RN4; Agustín con tres ACTIVAS para RN5. El alta/baja y la baja de socio usaron personas ficticias adicionales, sin alterar el perfil de los casos de reglas. Ver [IDs y precondiciones reales](../Docs/entrega/evidencias/escenarios.json) y [procedimiento de sistema](../Docs/pruebas-sistema.md).

Los valores del entorno general siguen siendo ejemplos. No asumir que los IDs de otra computadora coinciden. Completar las variables rn1SocioId/rn1ActividadId y equivalentes, socioId/actividadId y socioBajaId después de revisar los datos elegidos. ABM captura actividadCreadaId y alta captura inscripcionCreadaId; la colección crea, edita y elimina una actividad propia sin referencias.

RF3 incluye recepción 201, DNI inválido 400 y actividad inexistente 404. La solicitud no consume cupo. Migración 004 requerida en una base anterior; schema.sql basta en una base nueva. 002/003 no se ejecutan. No hay secretos en colección o entorno.

Puede ejecutarse con Newman instalado aparte de las aplicaciones:

```powershell
newman run postman/SocioClub.postman_collection.json -e RUTA_AL_ENTORNO_DE_ENSAYO_REVISADO.json
```

Solo después de verificar que el entorno apunta al backend aislado y que todos sus escenarios están preparados. No confundir HTTP válido con cumplimiento si no se verifican los estados previos/posteriores. Las bajas son lógicas y no existe reactivación automática de socio.
